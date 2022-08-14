package com.rmsr.myguard.data.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
import android.net.NetworkRequest
import android.os.Build
import com.rmsr.myguard.domain.utils.ConnectionMonitor
import com.rmsr.myguard.domain.utils.NetworkStatus
import com.rmsr.myguard.utils.MyLog
import com.rmsr.myguard.utils.RxSchedulers
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConnectionMonitoring @Inject constructor(
    @ApplicationContext context: Context,
    rxSchedulers: RxSchedulers
) : ConnectionMonitor {

    private val connectivityManager: ConnectivityManager =
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    private val TAG = "Rob_ConnectionMonitor"

    private val networkStateSubject =
        BehaviorSubject.createDefault(NetworkStatus.Checking as NetworkStatus)

    private val networkMainCallback by lazy {
        createNetworkCallback(onChange = networkStateSubject::onNext)
    }

    private val stream = networkStateSubject
        .subscribeOn(rxSchedulers.io())

        .doOnSubscribe { registerMonitor() }

        .doFinally { unregisterMonitor() }

        .distinctUntilChanged()
        // re hotting, to make behavior subject responsible for monitoring registrations.
        .share()


    override fun observeState(): Observable<NetworkStatus> = stream

    override val status: NetworkStatus
        get() = when {
            networkStateSubject.hasObservers() -> networkStateSubject.value!!
            else -> getNetworkStatus(connectivityManager.activeNetwork)
        }


    private fun registerMonitor(callback: ConnectivityManager.NetworkCallback = networkMainCallback) {
        MyLog.d(TAG, "registered")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(callback)
            return
        }

        val networkRequest = NetworkRequest.Builder().apply {
            addCapability(NET_CAPABILITY_INTERNET)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                addCapability(NET_CAPABILITY_VALIDATED)

        }.build()

        connectivityManager.registerNetworkCallback(networkRequest, callback)
    }

    private fun unregisterMonitor(callback: ConnectivityManager.NetworkCallback = networkMainCallback) {
        connectivityManager.unregisterNetworkCallback(callback)
        MyLog.d(TAG, "unregistered")
    }

    private fun createNetworkCallback(onChange: (NetworkStatus) -> Unit): ConnectivityManager.NetworkCallback =
        object : ConnectivityManager.NetworkCallback() {

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
                    return

                val state = getNetworkStatus(networkCapabilities)
                onChange(state)
            }

            // onAvailable() is only used if sdk less than Oreo,
            // else we depend on onCapabilitiesChanged()
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    return

                val state = getNetworkStatus(network)
                onChange(state)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                onChange(NetworkStatus.Offline)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                onChange(NetworkStatus.Offline)
            }

        }

    private fun getNetworkStatus(network: Network?): NetworkStatus {
        val capabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return NetworkStatus.Offline

        return getNetworkStatus(capabilities)
    }

    private fun getNetworkStatus(networkCapabilities: NetworkCapabilities): NetworkStatus {
        val isInternet = networkCapabilities.hasCapability(NET_CAPABILITY_INTERNET)

        val isValidated = when {

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                networkCapabilities.hasCapability(NET_CAPABILITY_VALIDATED)

            else ->
                connectivityManager.activeNetworkInfo
                    ?.let { it.isAvailable && it.isConnected } ?: false
        }

        return if (isInternet && isValidated) NetworkStatus.Online else NetworkStatus.Offline
    }

}
