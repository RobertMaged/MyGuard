package com.rmsr.myguard.domain.repository

import com.rmsr.myguard.domain.entity.Schedule
import com.rmsr.myguard.domain.entity.ScheduleId
import com.rmsr.myguard.domain.entity.Session
import com.rmsr.myguard.domain.entity.Session.SessionInfo
import com.rmsr.myguard.domain.entity.SessionId
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.MaybeObserver
import io.reactivex.rxjava3.core.Single

interface SessionRepository {

    /**
     * Create a new [Session] with empty values so it can have an id,
     * which can be used to updated [Session] later or case needed in relations with other entities.
     *
     * @return Id of the created session wrapped in [SessionId].
     * @see updateSession
     */
    fun initNewScanSession(): Single<SessionId>

    /**
     * @param scanSession session to edit which already existed.
     * @see initNewScanSession if you need to create new session first.
     */
    fun updateSession(scanSession: Session): Completable

    /**
     * Keep reference for all scanned [Schedule]s in this scan session,
     * so later we can know scanned schedules in specific session.
     *
     * @param sessionId id of current scan session needed to keep reference for.
     * @param schedulesIds unique ids of scanned schedules in specific session needed to keep reference for.
     */
    fun saveSessionScannedSchedules(
        sessionId: SessionId,
        schedulesIds: Set<ScheduleId>
    ): Completable

    /**
     * @param sessionId id of needed [Session].
     * @return [MaybeObserver.onSuccess] with specified session,
     *  or [MaybeObserver.onComplete] if no session found with this Id.
     */
    fun getSession(sessionId: SessionId): Maybe<Session>

    /**
     * @param sessionsIds ids of needed [Session]s.
     * @return [MaybeObserver.onSuccess] with specified sessions,
     * or [MaybeObserver.onComplete] if no sessions found with this Ids.
     */
    fun getSessions(sessionsIds: List<SessionId>): Maybe<List<Session>>

    /**
     * Return last created scan [Session] by [SessionInfo.createdTime].
     *
     * @return [MaybeObserver.onSuccess] with last session,
     * or [MaybeObserver.onComplete] if no scan ever happened.
     */
    fun getLastSession(): Maybe<Session>

    @Deprecated("getSession() first then update it with updateSession().")
    fun markSessionUserResponded(sessionId: Long): Completable
}

