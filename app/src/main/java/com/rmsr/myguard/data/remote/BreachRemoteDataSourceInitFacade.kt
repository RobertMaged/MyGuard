package com.rmsr.myguard.data.remote

import com.rmsr.myguard.data.database.dao.BreachDao
import com.rmsr.myguard.data.database.entity.BreachEntity
import com.rmsr.myguard.data.mapper.Mapper
import com.rmsr.myguard.data.remote.pojo_response.BreachResponse
import com.rmsr.myguard.domain.entity.errors.ErrorEntity
import com.rmsr.myguard.utils.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

data class BreachRemoteDataSourceInitFacade @Inject constructor(
    private val breachApiLazy: dagger.Lazy<BreachesRemoteDataSource>,
    private val breachDao: BreachDao,
    private val breachMapper: Mapper<BreachResponse, BreachEntity>,
    private val fire: IFire,
    private val rxSchedulers: RxSchedulers,
) {
    val api: BreachesRemoteDataSource
        get() = breachApiLazy.get()

    private var initState by AtomicReference(ApisInitState())


    fun initIfNot(): Completable {
        if (initState.isAllInit) return Completable.complete()

        val initOperations = buildList<Completable> {
            if (!initState.userAuthenticated)
                add(
                    fire.ensureUserSignedIn()
                        .doOnComplete { initState = initState.copy(userAuthenticated = true) }
                )

            if (!initState.tokensInitialized)
                add(
                    fire.initAppApis().doOnComplete {
                        initState = initState.copy(tokensInitialized = true)
                    }
                )
        }

        return Completable.concatArray(
            *initOperations.toTypedArray(),
        ).observeOn(rxSchedulers.io()).mergeWith(refreshDataIfNot())
            .doOnComplete { MyLog.d(TAG, "initApiFire: done", logThreadName = true) }
            .doOnError { MyLog.e(TAG, "initApiFire: error-> $it", logThreadName = true) }
    }

    private fun refreshDataIfNot(): Completable = when {
        initState.isDataRefreshed -> Completable.complete()

        else -> refreshBreaches()
            .doOnComplete { initState = initState.copy(isDataRefreshed = true) }
    }

    private fun refreshBreaches(): Completable {
        return api.getAllBreachesFromApi()
            .onErrorResumeNext {
                Single.error(
                    when (it) {
                        is IOException -> {
                            if (it.message?.contains("No Internet Connection") == true)
                                ErrorEntity.NoNetwork
                            else
                                ErrorEntity.UnKnownError(it.message.orEmpty(), it.cause)
                        }
                        is HttpException -> LeaksApiError.handleResponse(it)
                        else -> ErrorEntity.UnKnownError(it.message.orEmpty(), it.cause)
                    }
                )
            }

            .map { breachResponseList -> breachMapper.map(breachResponseList) }
            .flatMapCompletable { allBreaches ->
                insertBreaches(allBreaches)
            }
    }

    private fun insertBreaches(list: List<BreachEntity>): Completable {
        return Single.just(list)
            .timestamp()
            .map { (time, breaches) ->
                breaches.map { it.copy(createdTime = time) }
            }
            .flatMapCompletable { breachEntities ->
                breachDao.insert(breachEntities)
            }
    }

    private data class ApisInitState(
        val userAuthenticated: Boolean = false,
        val tokensInitialized: Boolean = false,
        val isDataRefreshed: Boolean = false
    ) {
        val isAllInit: Boolean
            get() = userAuthenticated && tokensInitialized && isDataRefreshed
    }

    companion object {
        private const val TAG = "Rob_ApiServiceFacade"
    }
}
