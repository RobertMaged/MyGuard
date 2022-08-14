package com.rmsr.myguard.data.database.history_caching

import com.rmsr.myguard.data.database.entity.QueryEntity
import io.reactivex.rxjava3.core.Completable


interface HistoryCachingStrategy {
    fun cacheQueryWithFoundBreaches(
        queryEntity: QueryEntity,
        resultBreachesId: List<Long>,
    ): Completable
}

