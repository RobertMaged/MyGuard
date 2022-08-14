package com.rmsr.myguard.data.remote

import com.rmsr.myguard.data.remote.pojo_response.BreachResponse
import com.rmsr.myguard.data.remote.pojo_response.PasteResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BreachesRemoteDataSource {

    //    @GET("/api/v3/breachedaccount/{accountOrPhone}")
    @GET("$BREACH_ACCOUNT_SERVICE/{accountOrPhone}")
    fun getBreachesByAccountOrPhone(
        @Path("accountOrPhone") query: String,
        @Query("truncateResponse") truncate: Boolean = true
    ): Single<List<BreachResponse>>

    //    @GET("/api/v3/breaches")
    @GET(ALL_BREACHES)
    fun getAllBreachesFromApi(): Single<List<BreachResponse>>

    @GET("/api/v3/pasteaccount/{account}")
    fun getPastesByAccount(@Path("account") account: String): Single<List<PasteResponse>>


    //not sure If used
    @GET("/api/v3/dataclasses")
    fun getAllCompromisedDataClasses(): Single<List<String>>

    @GET("/api/v3/breaches")
    fun getBreachByDomain(@Query("domain") domainName: String): Single<List<BreachResponse>>

    @GET("/api/v3/breach/{name}")
    fun getBreachByName(@Path("name") name: String): Single<BreachResponse>

    companion object {
        const val API_BREACHES_BASE_URL = "https://haveibeenpwned.com"
        const val VERSION = "/api/v3"
        const val BREACH_ACCOUNT_SERVICE = "/api/v3/breachedaccount"
        const val ALL_BREACHES = "/api/v3/breaches"

        val AUTH_PATHS = arrayOf(
            "$BREACH_ACCOUNT_SERVICE/"
        )
    }
}

