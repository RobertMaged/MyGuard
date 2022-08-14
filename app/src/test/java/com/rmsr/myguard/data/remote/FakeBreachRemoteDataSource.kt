package com.rmsr.myguard.data.remote

import com.rmsr.myguard.data.remote.pojo_response.BreachResponse
import com.rmsr.myguard.data.remote.pojo_response.PasteResponse
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

class FakeBreachRemoteDataSource(initBreaches: List<BreachResponse>) : BreachesRemoteDataSource {
    private val breaches = initBreaches.toSet()
    private val half = breaches.size / 2
    private val queries = mapOf(
        firstHalfLeaksQuery to { breaches.take(half) },
        secondHalfLeaksQuery to { breaches.drop(half) },
        firstLeakQuery to { listOf(breaches.first()) },
        lastLeakQuery to { listOf(breaches.last()) },
        randomLeakQuery to { listOf(breaches.shuffled().first()) },
        randomHalfLeaksQuery to { breaches.shuffled().take(half) },
    )

    override fun getBreachesByAccountOrPhone(
        query: String,
        truncate: Boolean
    ): Single<List<BreachResponse>> = if (query in queries.keys) {
        val list = queries.getValue(query).invoke()
        Single.just(list)
    } else {
        val responseError = Response.error<List<BreachResponse>>(
            404,
            ResponseBody.create(null, "Not Found")
        )
        Single.error(
            HttpException(
                responseError
            )
        )
    }


    override fun getAllBreachesFromApi(): Single<List<BreachResponse>> {
        return Single.just(breaches.toList())
    }

    override fun getPastesByAccount(account: String): Single<List<PasteResponse>> {
        throw NotImplementedError("Not yet implemented")
    }

    override fun getAllCompromisedDataClasses(): Single<List<String>> {
        throw NotImplementedError("Not yet implemented")
    }

    override fun getBreachByDomain(domainName: String): Single<List<BreachResponse>> {
        throw NotImplementedError("Not yet implemented")
    }

    override fun getBreachByName(name: String): Single<BreachResponse> {
        throw NotImplementedError("Not yet implemented")
    }

    companion object {
        const val firstHalfLeaksQuery = "firsthalf@gmail.com"
        const val secondHalfLeaksQuery = "secondhalf@gmail.com"
        const val firstLeakQuery = "first@gmail.com"
        const val lastLeakQuery = "second@gmail.com"
        const val randomLeakQuery = "randomLeak@gmail.com"
        const val randomHalfLeaksQuery = "randomHalfLeaks@gmail.com"
    }
}