package com.rmsr.myguard.data.database.dao

import androidx.room.*
import com.rmsr.myguard.data.database.entity.QueryEntity
import com.rmsr.myguard.data.database.entity.SearchHistoryEntity
import com.rmsr.myguard.data.database.entity.relations.HistoryWithQuery
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

@Dao
abstract class SearchHistoryDao {

    private val history = SearchHistoryEntity.SCHEMA
    private val query = QueryEntity.SCHEMA

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insert(history: SearchHistoryEntity): Single<Long>

    fun insertOrGet(queryId: Long): Single<Long> {
        return getHistory(historyQueryId = queryId)
            .map { it.queryId }
            .switchIfEmpty(
                Single.just(queryId)
                    .timestamp()
                    .map { idTimed ->
                        SearchHistoryEntity(
                            queryId = idTimed.value(), accessCount = 0,
                            createdTime = idTimed.time(), lastAccessTime = idTimed.time()
                        )
                    }
                    .flatMap { searchEntity ->
                        insert(history = searchEntity)
                    }
            )
    }

    @Query("UPDATE ${history.TABLE_NAME} SET ${history.LAST_ACCESS_TIME} =:accessTime, ${history.ACCESS_COUNT} = ${history.ACCESS_COUNT}+1 WHERE ${history.QUERY_ID} = :historyQueryId")
    abstract fun incrementHistoryAccess(
        historyQueryId: Long,
        accessTime: Long = System.currentTimeMillis(),
    ): Completable

    @Update
    abstract fun update(history: SearchHistoryEntity): Completable

    @Query("DELETE FROM ${history.TABLE_NAME}")
    abstract fun clearHistory(): Single<Int>

    //////////////////////// retrieve ///////////////////////////

    @Query("SELECT * FROM ${history.TABLE_NAME} WHERE ${history.QUERY_ID} =:historyQueryId")
    abstract fun getHistory(historyQueryId: Long): Maybe<SearchHistoryEntity>

    @Query("SELECT * FROM ${history.TABLE_NAME}")
    abstract fun allHistory(): Maybe<List<SearchHistoryEntity>>

    //TODO add limit or pagination.
    @Query("SELECT ${query.CONTENT} FROM ${history.TABLE_NAME} INNER JOIN ${query.TABLE_NAME} ON ${query.ID} = ${history.QUERY_ID} ORDER BY ${history.LAST_ACCESS_TIME} DESC;")
    abstract fun getSearchStack(): Flowable<List<String>>

    @Query("SELECT ${history.TABLE_NAME}.*, ${query.TABLE_NAME}.* FROM ${history.TABLE_NAME} INNER JOIN ${query.TABLE_NAME} ON ${query.ID} = ${history.QUERY_ID} ORDER BY ${history.LAST_ACCESS_TIME} DESC;")
    abstract fun observeHistory(): Flowable<List<HistoryWithQuery>>

}