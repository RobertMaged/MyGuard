package com.rmsr.myguard.domain.repository

import com.rmsr.myguard.domain.entity.*
import com.rmsr.myguard.domain.entity.errors.NotExistedReferenceError
import io.reactivex.rxjava3.core.*

interface ScanRecordsRepository {

    /**
     * Create a new list of [ScanRecord]s, each contain already existed references
     * of [Breach] found for scanned [Schedule] during Specific [Session].
     *
     * If same [ScanRecord.Identifiers.scheduleId] and [ScanRecord.Identifiers.breachId] combination already existed,
     * it will be ignored during creation because its already created at earlier [Session].
     *
     * @param scanRecords list of records to create.
     * @throws NotExistedReferenceError if one of Ids is not exist.
     */
    fun createScanRecords(scanRecords: Set<ScanRecord>): Completable

    /**
     * @param scanRecords records to edit which is already exist.
     * @see createScanRecords
     */
    fun updateScanRecords(scanRecords: Set<ScanRecord>): Completable

    /**
     * Keep updated with changes happen to this schedules [ScanRecord]s.
     *
     * This flowable never calls [FlowableSubscriber.onComplete].
     *
     * @return [ScanRecord]s of interested schedules, or empty list if no record created for this schedules.
     */
    fun observeSchedulesRecords(schedulesIds: List<ScheduleId>): Flowable<List<ScanRecord>>

    /**
     * Return all [ScanRecord]s created during this specified session or empty if no new [Breach]s found for scanned [Schedule]s.
     *
     * @param sessionId session to get all its records.
     * @return [MaybeObserver.onSuccess] with specified records,
     *      or [MaybeObserver.onComplete] if no records created during this session.
     */
    fun getSessionRecords(sessionId: SessionId): Maybe<List<ScanRecord>>

    /**
     * Return all [Schedule] created [ScanRecord]s or empty if this schedule has no [Breach]s.
     *
     * @param scheduleId schedule to get all its records.
     * @return [MaybeObserver.onSuccess] with specified records,
     *      or [MaybeObserver.onComplete] if no records created for this [Schedule].
     */
    fun getScheduleRecords(scheduleId: ScheduleId): Maybe<List<ScanRecord>>

    @Deprecated("depend on update now")
    fun scheduleRecordsAcknowledged(scheduleId: ScheduleId): Completable

    @Deprecated("depend on update now")
    fun sessionRecordsNotified(sessionId: Long): Completable
}
