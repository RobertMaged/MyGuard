package com.rmsr.myguard.data.database.history_caching

import com.rmsr.myguard.data.database.entity.QueryEntity
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class HistoryDisabledStrategy @Inject constructor() : HistoryCachingStrategy {

    override fun cacheQueryWithFoundBreaches(
        queryEntity: QueryEntity,
        resultBreachesId: List<Long>,
    ): Completable = Completable.complete()
}