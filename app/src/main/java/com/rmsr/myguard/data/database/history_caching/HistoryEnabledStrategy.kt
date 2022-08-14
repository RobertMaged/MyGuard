package com.rmsr.myguard.data.database.history_caching

import com.rmsr.myguard.data.database.dao.HistoryBreachDao
import com.rmsr.myguard.data.database.dao.QueryDao
import com.rmsr.myguard.data.database.dao.SearchHistoryDao
import com.rmsr.myguard.data.database.entity.QueryEntity
import com.rmsr.myguard.data.database.entity.relations.HistoryBreachXRef
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class HistoryEnabledStrategy @Inject constructor(
    private val queryDao: QueryDao,
    private val historyDao: SearchHistoryDao,
    private val historyBreachXrefDao: HistoryBreachDao,
) : HistoryCachingStrategy {

    override fun cacheQueryWithFoundBreaches(
        queryEntity: QueryEntity,
        resultBreachesId: List<Long>,
    ): Completable {

        return saveToSearchHistory(queryEntity)
            .flatMapCompletable { queryId ->
                saveHistoryWithBreachesRelations(
                    historyQueryId = queryId,
                    breachesId = resultBreachesId
                )
            }

    }

    /**
     * @return inserted history id.
     */
    private fun saveToSearchHistory(
        queryEntity: QueryEntity
    ): Single<Long> {

//        return Single.just(
//            QueryEntity(content = query, type = type)
//        )
        //create new query and get id.
        return queryDao.insertOrGet(queryEntity = queryEntity)
            //create search history record based on query id, and return its id.
            .flatMap { queryId -> historyDao.insertOrGet(queryId = queryId) }
            //satisfy history tracking
            .flatMap { queryId ->
                historyDao.incrementHistoryAccess(historyQueryId = queryId)
                    .toSingleDefault(queryId)
            }

    }

    private fun saveHistoryWithBreachesRelations(
        historyQueryId: Long,
        breachesId: List<Long>,
    ): Completable {
        if (breachesId.isEmpty()) return Completable.complete()

        val xRefs = breachesId.map { breachId ->
            HistoryBreachXRef(
                historyId = historyQueryId,
                breachId = breachId
            )
        }
        return historyBreachXrefDao.insertHistoryBreachRef(xRefs)
    }

//    private fun searchAndCacheResult(
//        queryId: Long,
//        searchSource: () -> Maybe<List<BreachEntity>>
//    ): Maybe<List<BreachEntity>> {
//        return searchSource()
//            .flatMapSingle { entities ->
//                saveQueryWithBreachesRelation(queryId, entities.map { it.id })
//                    .toSingleDefault(entities)
//            }
//    }


}
