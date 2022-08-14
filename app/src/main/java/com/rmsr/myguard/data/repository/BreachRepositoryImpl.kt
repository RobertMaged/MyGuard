package com.rmsr.myguard.data.repository

import com.google.common.util.concurrent.RateLimiter
import com.rmsr.myguard.BuildConfig
import com.rmsr.myguard.data.database.dao.BreachDao
import com.rmsr.myguard.data.database.entity.BreachEntity
import com.rmsr.myguard.data.database.entity.QueryEntity
import com.rmsr.myguard.data.database.history_caching.HistoryCachingStrategy
import com.rmsr.myguard.data.database.history_caching.HistoryCachingStrategyFactory
import com.rmsr.myguard.data.mapper.BreachRepositoryMappers
import com.rmsr.myguard.data.remote.BreachRemoteDataSourceInitFacade
import com.rmsr.myguard.data.remote.LeaksApiError
import com.rmsr.myguard.data.remote.pojo_response.BreachResponse
import com.rmsr.myguard.domain.entity.Breach
import com.rmsr.myguard.domain.entity.BreachId
import com.rmsr.myguard.domain.entity.QueryType
import com.rmsr.myguard.domain.entity.SearchQuery
import com.rmsr.myguard.domain.entity.errors.ErrorEntity
import com.rmsr.myguard.domain.repository.BreachRepository
import com.rmsr.myguard.domain.utils.HistoryMode
import com.rmsr.myguard.utils.MyLog
import com.rmsr.myguard.utils.RxSchedulers
import com.rmsr.myguard.utils.onEmptyListComplete
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleSource
import io.reactivex.rxjava3.core.SingleTransformer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BreachRepositoryImpl constructor(
    private val breachApiFacade: BreachRemoteDataSourceInitFacade,
    private val breachDao: BreachDao,
    private val mappers: BreachRepositoryMappers,
    private val rateLimitManager: SingleTransformer<List<BreachResponse>, List<BreachResponse>>,
    private val historyStrategy: HistoryCachingStrategyFactory,
    private val rxSchedulers: RxSchedulers,
) : BreachRepository {

    @Inject
    constructor(
        breachApiFacade: BreachRemoteDataSourceInitFacade,
        breachDao: BreachDao,
        mappers: BreachRepositoryMappers,
        historyStrategy: HistoryCachingStrategyFactory,
        rxSchedulers: RxSchedulers,
    ) : this(
        breachApiFacade,
        breachDao,
        mappers,
        ApiRateLimitManager(),
        historyStrategy,
        rxSchedulers
    )


    override fun searchForLeaks(
        query: SearchQuery,
        historyMode: HistoryMode,
    ): Maybe<List<Breach>> {
        val queryEntity = mappers.queryEntity.mapReversed(query)

        return when (queryEntity.type) {

            QueryType.EMAIL, QueryType.Phone ->
                runIfDataUpdated { getBreachesByEmailOrPhone(queryEntity.content) }

            QueryType.DOMAIN ->
                runWhateverDataUpdated { breachDao.searchBreachByDomain(queryEntity.content) }

            QueryType.DOMAIN_NAME ->
                runWhateverDataUpdated { breachDao.searchBreachByDomainName(queryEntity.content) }
        }

            .subscribeOn(rxSchedulers.io())

            .saveToHistory(queryEntity, historyMode)

            .onEmptyListComplete()

            //map breach entities to domain.
            .map { entityList: List<BreachEntity> ->
                mappers.breachEntity.map(entityList)
            }


    }

    override fun searchForLeaksIds(
        query: SearchQuery,
        historyMode: HistoryMode,
    ): Maybe<List<BreachId>> {
        val queryEntity = mappers.queryEntity.mapReversed(query)

        return when (queryEntity.type) {

            QueryType.EMAIL, QueryType.Phone ->
                runIfDataUpdated { getBreachesIdsByEmailOrPhone(queryEntity.content) }

            QueryType.DOMAIN ->
                runWhateverDataUpdated { breachDao.searchBreachIdByDomain(queryEntity.content) }

            QueryType.DOMAIN_NAME ->
                runWhateverDataUpdated { breachDao.searchBreachIdByDomainName(queryEntity.content) }
        }
            .subscribeOn(rxSchedulers.io())

            .saveIdsToHistory(queryEntity, mode = historyMode)

            .onEmptyListComplete()

            .map { it.map { BreachId(it) } }
    }

    override fun getAllBreaches(): Maybe<List<Breach>> {
        return breachDao.getAllBreaches()
            .subscribeOn(rxSchedulers.io())
            .onEmptyListComplete()
            .map { mappers.breachEntity.map(it) }
    }

    override fun getBreachById(breachId: BreachId): Maybe<Breach> {
        return breachDao.getBreachById(breachId.value)
            .subscribeOn(rxSchedulers.io())
            .map { mappers.breachEntity.map(it) }
    }

    override fun getBreachesByIds(breachesIds: Iterable<Long>): Maybe<List<Breach>> {
        return breachDao.getBreachesByIds(breachesIds = breachesIds.toList())
            .subscribeOn(rxSchedulers.io())
            .onEmptyListComplete()
            .map { mappers.breachEntity.map(it) }
    }

    override fun getBreachesByIds(breachesIds: List<BreachId>): Maybe<List<Breach>> {
        return Single.just(breachesIds)
            .subscribeOn(rxSchedulers.io())

            .map { it: List<BreachId> -> it.map { it.value } }
            .flatMapMaybe(breachDao::getBreachesByIds)
            .onEmptyListComplete()

            .map { breachEntities -> mappers.breachEntity.map(breachEntities) }
    }

    private inline fun <reified T> runIfDataUpdated(crossinline function: () -> Maybe<T>): Maybe<T> {
        return breachApiFacade.initIfNot().andThen(function())
    }

    private inline fun <reified T> runWhateverDataUpdated(crossinline function: () -> Maybe<T>): Maybe<T> {
        return breachApiFacade.initIfNot().onErrorComplete().andThen(function())
    }


    private fun getBreachesByEmailOrPhone(query: String): Maybe<List<BreachEntity>> {

        return searchForBreachesNamesByEmailOrPhone(query = query)
            .flatMap(breachDao::getBreachesByNames)
    }

    private fun getBreachesIdsByEmailOrPhone(query: String): Maybe<List<Long>> {
        return searchForBreachesNamesByEmailOrPhone(query = query)
            .flatMap(breachDao::getBreachesIdsByNames)
    }


    private fun searchForBreachesNamesByEmailOrPhone(query: String): Maybe<List<String>> {
        return breachApiFacade.api
            .getBreachesByAccountOrPhone(query = query, truncate = true)

            .compose(rateLimitManager)

            .toMaybe()

            .onErrorResumeNext { apiResponseErrorHandler(it) }

            .map { responseList -> responseList.map { it.name } }
    }

    private fun apiResponseErrorHandler(thr: Throwable): Maybe<List<BreachResponse>> {
        val error = when (thr) {
            is IOException -> {
                if (thr.message?.contains("No Internet Connection") == true)
                    ErrorEntity.NoNetwork
                else
                    ErrorEntity.UnKnownError(thr.message.orEmpty(), thr.cause)
            }

            is HttpException -> {
                val respError = LeaksApiError.handleResponse(thr)

                if (respError is LeaksApiError.NoLeaksFound)
                    return Maybe.empty()

                ErrorEntity.ApiError(
                    respError.responseCode,
                    respError.message.orEmpty(),
                    respError.cause
                )
            }

            else -> ErrorEntity.UnKnownError(thr.message.orEmpty(), thr.cause)
        }

        MyLog.w(TAG, "Api Response error: ${thr.message}", thr)

        return Maybe.error(error)
    }


    /**
     * Cache the result with searched query to history using [HistoryCachingStrategy].
     *
     * No changes made to the flow.
     */
    private fun <T : List<BreachEntity>> Maybe<T>.saveToHistory(
        queryEntity: QueryEntity, mode: HistoryMode,
    ): Maybe<T> {
        val cachingStrategy = historyStrategy.fromMode(mode)

        return this.flatMapSingle { entityList ->
            cachingStrategy.cacheQueryWithFoundBreaches(
                queryEntity = queryEntity,
                resultBreachesId = entityList.map { it.id }
            )
                //just keep the flow as it is with the entities result.
                .toSingleDefault(entityList)
        }
            //case query have no leaks.
            .switchIfEmpty(
                Maybe.fromCompletable(
                    cachingStrategy.cacheQueryWithFoundBreaches(
                        queryEntity = queryEntity,
                        resultBreachesId = emptyList()
                    )
                )
            )
    }

    /**
     * Cache the result with searched query to history using [HistoryCachingStrategy].
     *
     * No changes made to the flow.
     */
    private fun <T : List<Long>> Maybe<T>.saveIdsToHistory(
        queryEntity: QueryEntity, mode: HistoryMode,
    ): Maybe<T> {
        val cachingStrategy = historyStrategy.fromMode(mode)

        return this.flatMapSingle { entityList ->
            cachingStrategy.cacheQueryWithFoundBreaches(
                queryEntity = queryEntity,
                resultBreachesId = entityList
            )
                //just keep the flow as it is with the entities result.
                .toSingleDefault(entityList)
        }
            //case query have no leaks.
            .switchIfEmpty(
                Maybe.fromCompletable(
                    cachingStrategy.cacheQueryWithFoundBreaches(
                        queryEntity = queryEntity,
                        resultBreachesId = emptyList()
                    )
                )
            )
    }


    private class ApiRateLimitManager :
        SingleTransformer<List<BreachResponse>, List<BreachResponse>> {

        private val defaultRate = calculateQPS(API_BREACHES_RATE_LIMIT_MILLIS)

        private val limiter = RateLimiter.create(defaultRate)

        private val delayedRequestsDisposables = CompositeDisposable()

        override fun apply(upstream: Single<List<BreachResponse>>): SingleSource<List<BreachResponse>> {

            var disposable: Disposable? = null

            return upstream
                //api request is hit so remove it from delayed(waiting) requests.
                .doOnSubscribe {
                    disposable.invokeUnsafeNullOnDebug(delayedRequestsDisposables::delete)
                }

                .delaySubscription(
                    Single.just(1).map { limiter.acquire() }
                )

                .doOnSubscribe { disposable = it.also(delayedRequestsDisposables::add) }

                //only executed if RateLimit error response
                .doOnError { thr ->

                    if (thr !is HttpException) return@doOnError

                    val errorResponse = LeaksApiError.handleResponse(thr)
                    if (errorResponse !is LeaksApiError.TooManyRequests)
                        return@doOnError


                    val newWaitMillis =
                        maxOf(API_BREACHES_RATE_LIMIT_MILLIS, errorResponse.waitMilliSeconds)

                    limiter.rate = calculateQPS(newWaitMillis)

                    //to make sure any acquires with previous limit are consumed.
                    limiter.tryAcquire()

                    //cancel all remaining requests, current is not included.
                    delayedRequestsDisposables.clear()

                }

                //reset rate limit only on next success api response.
                .doOnSuccess {
                    if (limiter.rate != defaultRate)
                        limiter.rate = defaultRate
                }
                //NoLeaksFound also represent success api response.
                .doOnError {
                    if (it !is HttpException) return@doOnError

                    if (LeaksApiError.handleResponse(it) !is LeaksApiError.NoLeaksFound) return@doOnError

                    if (limiter.rate != defaultRate)
                        limiter.rate = defaultRate
                }

                //only useful when down stream dispose request while it's in delay and not called api yet.
                .doFinally {
                    disposable.invokeUnsafeNullOnDebug(delayedRequestsDisposables::delete)
                }
        }

        private fun calculateQPS(millis: Int) = (1.0 / millis.toDouble()) * 1000.0

        private inline infix fun <reified T> T?.invokeUnsafeNullOnDebug(block: (T) -> Unit) {
            if (BuildConfig.DEBUG)
                block(this!!)
            else
                this?.let { block(it) }
        }
    }


    companion object {
        /**
         * API is limited to one per every 1500 milliseconds,
         * 100 milliseconds is added to make safe request without exceeding limit.
         */
        const val API_BREACHES_RATE_LIMIT_MILLIS: Int = 2000

        private const val TAG = "Rob_BreachRepository"
    }
}