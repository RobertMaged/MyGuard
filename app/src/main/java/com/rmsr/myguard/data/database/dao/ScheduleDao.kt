package com.rmsr.myguard.data.database.dao

import androidx.room.*
import com.rmsr.myguard.data.database.entity.QueryEntity
import com.rmsr.myguard.data.database.entity.ScheduleEntity
import com.rmsr.myguard.data.database.entity.relations.ScheduleWithQuery
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single


@Dao
abstract class ScheduleDao {
    private val query = QueryEntity.SCHEMA
    private val schedule = ScheduleEntity.SCHEMA

    @Insert
    abstract fun insert(scheduleEntity: ScheduleEntity): Single<Long>

    @Update
    abstract fun update(scheduleEntity: ScheduleEntity): Completable

    @Delete
    abstract fun delete(scheduleEntity: ScheduleEntity): Completable

    @Query("DELETE FROM ${schedule.TABLE_NAME} WHERE ${schedule.ID} =:scheduleId")
    abstract fun delete(scheduleId: Long): Completable

    @Query("SELECT ${schedule.ID} FROM ${schedule.TABLE_NAME} WHERE ${schedule.QUERY_ID} =:queryId")
    abstract fun findQueryScheduleId(queryId: Long): Maybe<Long>

    /**
     * Returns `true` if Schedules table has at least one element.
     */
    @Query("SELECT EXISTS (SELECT 1 FROM ${schedule.TABLE_NAME} LIMIT 1)")
    abstract fun any(): Single<Boolean>

    @Query("SELECT * FROM ${schedule.TABLE_NAME} WHERE ${schedule.ID} =:scheduleId")
    abstract fun getSchedule(scheduleId: Long): Maybe<ScheduleEntity>

    @Query("SELECT * FROM ${schedule.TABLE_NAME} WHERE ${schedule.ID} IN (:schedulesId)")
    abstract fun getSchedules(schedulesId: List<Long>): Maybe<List<ScheduleEntity>>

    @Query("SELECT * FROM ${schedule.TABLE_NAME} WHERE ${schedule.IS_MUTED} =:isMuted")
    abstract fun getSchedulesByMuteState(isMuted: Boolean = false): Maybe<List<ScheduleEntity>>

    @Query("SELECT * FROM ${schedule.TABLE_NAME} INNER JOIN ${query.TABLE_NAME} ON ${query.ID} = ${schedule.QUERY_ID} WHERE ${schedule.ID} =:scheduleId")
    abstract fun getScheduleWithQuery(scheduleId: Long): Maybe<ScheduleWithQuery>

    @Query("SELECT * FROM ${schedule.TABLE_NAME} INNER JOIN ${query.TABLE_NAME} ON ${query.ID} =  ${schedule.QUERY_ID} WHERE ${schedule.ID} IN (:schedulesId)")
    abstract fun getSchedulesWithQueries(schedulesId: List<Long>): Maybe<List<ScheduleWithQuery>>

    @Query("SELECT * FROM ${schedule.TABLE_NAME} INNER JOIN ${query.TABLE_NAME} ON ${query.ID} =  ${schedule.QUERY_ID}")
    abstract fun observeSchedulesWithQueries(): Flowable<List<ScheduleWithQuery>>


    /*
    @MapInfo(valueColumn = breach.TITLE)
    @Query("SELECT ${schedule.TABLE_NAME}.*, ${query.TABLE_NAME}.*, ${breach.TITLE} FROM ${schedule.TABLE_NAME} INNER JOIN ${query.TABLE_NAME} ON ${query.ID} = ${schedule.QUERY_ID} INNER JOIN ${record.TABLE_NAME} ON ${schedule.ID} = ${record.SCHEDULE_ID} AND ${record.USER_ACKNOWLEDGED} = $FALSE INNER JOIN ${breach.TABLE_NAME} ON ${record.BREACH_ID} = ${breach.ID}")
    abstract fun observeSchedulesWithUnAcknowledgedBreaches() : Flowable<Map<ScheduleWithQuery, List<String>>>
    */

    companion object {
        private const val TRUE = "TRUE"
        private const val FALSE = "FALSE"
    }
}