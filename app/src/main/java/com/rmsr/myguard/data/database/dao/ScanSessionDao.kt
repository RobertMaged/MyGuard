package com.rmsr.myguard.data.database.dao

import androidx.room.*
import com.rmsr.myguard.data.database.entity.ScanSessionEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single


@Dao
abstract class ScanSessionDao {

    private val session = ScanSessionEntity.SCHEMA

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(scanSession: ScanSessionEntity): Single<Long>

    @Update
    abstract fun update(scanSession: ScanSessionEntity): Completable

    @Deprecated("")
    @Query("UPDATE ${session.TABLE_NAME} SET ${session.USER_RESPOND} = $TRUE WHERE ${session.ID} =:sessionId")
    abstract fun userRespondedToSession(sessionId: Long): Completable

    /////////////////// Retrieve Section /////////////

    @Query("SELECT * FROM ${session.TABLE_NAME} WHERE ${session.ID} =:sessionId")
    abstract fun getSession(sessionId: Long): Maybe<ScanSessionEntity>

    @Query("SELECT * FROM ${session.TABLE_NAME} WHERE ${session.ID} IN (:sessionsIds)")
    abstract fun getSessions(sessionsIds: List<Long>): Maybe<List<ScanSessionEntity>>

    @Query("SELECT * FROM ${session.TABLE_NAME} ORDER BY ${session.CREATED} DESC LIMIT 1")
    abstract fun getLastSession(): Maybe<ScanSessionEntity>

    @Query("SELECT * FROM ${session.TABLE_NAME}")
    abstract fun allScanSessions(): Maybe<List<ScanSessionEntity>>

    companion object {
        private const val TRUE = "1"
        private const val FALSE = "0"
    }
}