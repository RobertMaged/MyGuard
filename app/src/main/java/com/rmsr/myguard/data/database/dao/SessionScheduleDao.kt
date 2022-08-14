package com.rmsr.myguard.data.database.dao

import androidx.room.*
import com.rmsr.myguard.data.database.entity.relations.SessionScheduleXRef
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import org.jetbrains.annotations.TestOnly

@Dao
abstract class SessionScheduleDao {

    private val xref = SessionScheduleXRef.SCHEMA

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertSessionScheduleRef(sessionScheduleList: List<SessionScheduleXRef>): Completable

    @Query("INSERT OR IGNORE INTO ${xref.TABLE_NAME}(${xref.SESSION_ID}, ${xref.SCHEDULE_ID}) VALUES (:sessionId, :scheduleId)")
    abstract fun insertSessionScheduleRef(sessionId: Long, scheduleId: Long): Completable

    @MapInfo(keyColumn = xref.SESSION_ID, valueColumn = xref.SCHEDULE_ID)
    @Query("SELECT ${xref.SESSION_ID}, ${xref.SCHEDULE_ID} FROM ${xref.TABLE_NAME} WHERE ${xref.SESSION_ID} IN (:sessionsIds)")
    abstract fun getSessionsXRefs(sessionsIds: List<Long>): Maybe<Map<Long, List<Long>>>

    @MapInfo(keyColumn = xref.SCHEDULE_ID, valueColumn = xref.SESSION_ID)
    @Query("SELECT ${xref.SCHEDULE_ID} , ${xref.SESSION_ID} FROM ${xref.TABLE_NAME} WHERE ${xref.SCHEDULE_ID} IN (:schedulesIds)")
    abstract fun getSchedulesXRefs(schedulesIds: List<Long>): Maybe<Map<Long, List<Long>>>

    //////////////////////
    @TestOnly
    @Query("SELECT * FROM ${xref.TABLE_NAME}")
    abstract fun getAll(): Maybe<List<SessionScheduleXRef>>
}