package com.rmsr.myguard.data.database.dao

import androidx.room.*
import com.rmsr.myguard.data.database.entity.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import org.jetbrains.annotations.TestOnly


@Dao
abstract class ScanRecordDao {

    private val record = ScheduleScanRecordEntity.SCHEMA
    private val breach = BreachEntity.SCHEMA
    private val schedule = ScheduleEntity.SCHEMA
    private val query = QueryEntity.SCHEMA
    private val session = ScanSessionEntity.SCHEMA


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(scanRecords: List<ScheduleScanRecordEntity>): Completable

    @Update
    abstract fun update(scanRecords: List<ScheduleScanRecordEntity>): Completable

    @Query("UPDATE ${record.TABLE_NAME} SET ${record.NOTIFY_TIME} = CURRENT_TIMESTAMP, ${record.USER_NOTIFIED} = $TRUE WHERE ${record.SESSION_ID} =:sessionId")
    abstract fun updateRecordsNotified(sessionId: Long): Completable

    @Query("SELECT ${record.TABLE_NAME}.* FROM ${record.TABLE_NAME} WHERE ${record.SESSION_ID} =:sessionId")
    abstract fun getSessionRecords(sessionId: Long): Maybe<List<ScheduleScanRecordEntity>>


    @Query("SELECT ${record.TABLE_NAME}.* FROM ${record.TABLE_NAME} WHERE ${record.SCHEDULE_ID} =:scheduleId")
    abstract fun getScheduleRecords(scheduleId: Long): Maybe<List<ScheduleScanRecordEntity>>

    @Query("SELECT ${record.TABLE_NAME}.* FROM ${record.TABLE_NAME}  WHERE ${record.SCHEDULE_ID} IN (:schedulesIds)")
    abstract fun observeSchedulesRecords(schedulesIds: List<Long>): Flowable<List<ScheduleScanRecordEntity>>

    ///////////////////////// relational /////////////////////////////////

    /**
     * if user acknowledged there for he must be notified too.
     */
    @Query("UPDATE ${record.TABLE_NAME} SET ${record.ACKNOWLEDGE_TIME} = CASE WHEN ${record.ACKNOWLEDGE_TIME} = 0 THEN CURRENT_TIMESTAMP ELSE ${record.ACKNOWLEDGE_TIME} END, ${record.USER_ACKNOWLEDGED} = $TRUE WHERE ${record.SCHEDULE_ID} =:scheduleId")
    abstract fun setScheduleRecordsAcknowledged(scheduleId: Long): Completable


    @Query("SELECT ${breach.ID} FROM ${breach.TABLE_NAME} INNER JOIN ${record.TABLE_NAME} ON ${record.BREACH_ID} = ${breach.ID} WHERE ${record.SCHEDULE_ID} =:scheduleId")
    abstract fun getScheduleAllBreachesIds(scheduleId: Long): Maybe<List<Long>>

    @Transaction
    @Query("SELECT ${breach.ID} FROM ${breach.TABLE_NAME} INNER JOIN ${record.TABLE_NAME} ON ${record.BREACH_ID} = ${breach.ID} WHERE ${record.SCHEDULE_ID} =:scheduleId AND ${record.USER_ACKNOWLEDGED} = $FALSE")
    abstract fun getScheduleUnacknowledgedBreachesIds(scheduleId: Long): Maybe<List<Long>>


    @Suppress("Maybe refactored.")
    @Query("SELECT ${schedule.ID}, ${breach.ID} FROM ${schedule.TABLE_NAME} LEFT OUTER JOIN ${record.TABLE_NAME} ON ${record.SCHEDULE_ID} = ${schedule.ID} AND ${record.USER_ACKNOWLEDGED} = $FALSE LEFT OUTER JOIN ${breach.TABLE_NAME} ON ${breach.ID} = ${record.BREACH_ID} WHERE ${schedule.ID} IN (:schedulesIds) ")
    @MapInfo(keyColumn = schedule.ID, valueColumn = breach.ID)
    abstract fun getSchedulesUnacknowledgedBreachesIds(schedulesIds: List<Long>): Maybe<Map<Long, List<Long>>>

    @Suppress("Not Used")
    @Query("SELECT ${breach.ID} FROM ${breach.TABLE_NAME} INNER JOIN ${record.TABLE_NAME} ON ${record.BREACH_ID} = ${breach.ID} WHERE ${record.SCHEDULE_ID} =:scheduleId AND ${record.USER_NOTIFIED} = $FALSE")
    abstract fun getScheduleUnNotifiedLeaksBreachesIds(scheduleId: Long): Maybe<List<Long>>


    @Query("SELECT ${breach.ID} FROM ${breach.TABLE_NAME} INNER JOIN ${record.TABLE_NAME} ON ${record.BREACH_ID} = ${breach.ID} WHERE ${record.SESSION_ID} = (SELECT i FROM (SELECT ${session.ID} as i, MAX(${session.CREATED}) FROM ${session.TABLE_NAME})) AND ${record.USER_NOTIFIED} = $FALSE")
    abstract fun getLastSessionUnNotifiedLeaksIds(): Maybe<List<Long>>

    @Suppress("Stay", "Not Used")
    @Query("SELECT ${breach.ID} FROM ${breach.TABLE_NAME} INNER JOIN ${record.TABLE_NAME} ON ${record.BREACH_ID} = ${breach.ID} WHERE ${record.SESSION_ID} IS NOT NULL AND ${record.USER_NOTIFIED} = $FALSE")
    abstract fun getAllUnNotifiedLeaks(): Maybe<List<Long>>

    @Query("SELECT ${breach.ID} FROM ${breach.TABLE_NAME} INNER JOIN ${record.TABLE_NAME} ON ${record.BREACH_ID} = ${breach.ID} WHERE ${record.SESSION_ID} =:sessionId AND ${record.USER_NOTIFIED} = $FALSE")
    abstract fun getSessionUnNotifiedLeaksIds(sessionId: Long): Maybe<List<Long>>

    @Query("SELECT ${breach.ID} FROM ${breach.TABLE_NAME} INNER JOIN ${record.TABLE_NAME} ON ${record.BREACH_ID} = ${breach.ID} WHERE ${record.SESSION_ID} =:sessionId")
    abstract fun getSessionAllLeaksIds(sessionId: Long): Maybe<List<Long>>

    ////////////////////////

    ////////////////////////

    @TestOnly
    @Query("SELECT ${record.TABLE_NAME}.* FROM ${record.TABLE_NAME}")
    abstract fun allRecords(): Maybe<List<ScheduleScanRecordEntity>>

    companion object {
        private const val TRUE = "1"
        private const val FALSE = "0"
    }
}