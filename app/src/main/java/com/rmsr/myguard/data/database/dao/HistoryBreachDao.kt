package com.rmsr.myguard.data.database.dao

import androidx.room.*
import com.rmsr.myguard.data.database.entity.relations.HistoryBreachXRef
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import org.jetbrains.annotations.TestOnly

@Dao
abstract class HistoryBreachDao {

    private val xref = HistoryBreachXRef.SCHEMA

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertHistoryBreachRef(historyBreachList: List<HistoryBreachXRef>): Completable

    @Query("INSERT OR IGNORE INTO ${xref.TABLE_NAME}(${xref.HISTORY_ID}, ${xref.BREACH_ID}) VALUES (:historyId, :breachId)")
    abstract fun insertHistoryBreachRef(historyId: Long, breachId: Long): Completable

    @MapInfo(keyColumn = xref.HISTORY_ID, valueColumn = xref.BREACH_ID)
    @Query("SELECT ${xref.HISTORY_ID}, ${xref.BREACH_ID} FROM ${xref.TABLE_NAME} WHERE ${xref.HISTORY_ID} IN (:historiesIds) ")
    abstract fun getHistoriesXRefs(historiesIds: List<Long>): Maybe<Map<Long, List<Long>>>

    @MapInfo(keyColumn = xref.BREACH_ID, valueColumn = xref.HISTORY_ID)
    @Query("SELECT ${xref.BREACH_ID}, ${xref.HISTORY_ID} FROM ${xref.TABLE_NAME} WHERE ${xref.BREACH_ID} IN (:breachesIds) ")
    abstract fun getBreachesXRefs(breachesIds: List<Long>): Maybe<Map<Long, List<Long>>>

    //////////////////////
    @TestOnly
    @Query("SELECT * FROM ${xref.TABLE_NAME}")
    abstract fun getAll(): Maybe<List<HistoryBreachXRef>>
}