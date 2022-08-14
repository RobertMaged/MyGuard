package com.rmsr.myguard.di

import android.os.Build
import com.rmsr.myguard.data.remote.BreachesRemoteDataSource
import com.rmsr.myguard.data.remote.Fire
import com.rmsr.myguard.data.remote.IFire
import com.rmsr.myguard.data.remote.PasswordsRemoteDataSource
import com.rmsr.myguard.domain.utils.ConnectionMonitor
import com.rmsr.myguard.domain.utils.NetworkStatus
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.security.KeyStore
import javax.inject.Singleton
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    @Singleton
    fun provideBreachesService(
        networkConnection: ConnectionMonitor,
        fire: IFire
    ): BreachesRemoteDataSource {

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                if (networkConnection.status != NetworkStatus.Online)
                    throw IOException("No Internet Connection")

                val builder = chain.request().newBuilder().apply {
                    val isAuthRequired =
                        chain.request().url() isPathIn BreachesRemoteDataSource.AUTH_PATHS

                    if (isAuthRequired.not()) return@apply


                    for (header in fire.authHeaders)
                        addHeader(header.key, header.value)
                }


                chain.proceed(builder.build())
            }


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {

            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null, null)

            val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
            val defaultTmf = TrustManagerFactory.getInstance(tmfAlgorithm)
            defaultTmf.init(null as KeyStore?)

            defaultTmf.trustManagers.filterIsInstance<X509TrustManager>()
                .flatMap { it.acceptedIssuers.toList() }
                .forEach { keyStore.setCertificateEntry(it.subjectDN.name, it) }

            val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
                ).build()


            val certificatePinner = CertificatePinner.Builder()
                .add("haveibeenpwned.com", *fire.pinners)
                .build()

            client
                .connectionSpecs(listOf(spec))
                .certificatePinner(certificatePinner)


        }

        return Retrofit.Builder()
            .baseUrl(BreachesRemoteDataSource.API_BREACHES_BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.build())
            .build()
            .create(BreachesRemoteDataSource::class.java)
    }

    @Provides
    @Singleton
    fun provideFire(): IFire = Fire

    @Provides
    @Singleton
    fun providePasswordsService(
        networkConnection: ConnectionMonitor,
    ): PasswordsRemoteDataSource {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                if (networkConnection.status != NetworkStatus.Online)
                    throw IOException("No Internet Connection")

                chain.proceed(chain.request().newBuilder().build())
            }

        return Retrofit.Builder()
            .baseUrl(PasswordsRemoteDataSource.API_PASSWORDS_BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous())
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(client.build())
            .build()
            .create(PasswordsRemoteDataSource::class.java)
    }

    private infix fun HttpUrl.isPathIn(paths: Array<String>): Boolean {
        val encodedPath = this.encodedPath()
        for (p in paths) {
            val isExist = encodedPath.startsWith(encodedPath, ignoreCase = true)

            if (isExist) return true
        }
        return false
    }
}
