package com.rmsr.myguard.data.repository

import com.rmsr.myguard.data.database.dao.ScanSessionDao
import com.rmsr.myguard.data.database.dao.SessionScheduleDao
import com.rmsr.myguard.data.database.entity.ScanSessionEntity
import com.rmsr.myguard.data.database.entity.relations.SessionScheduleXRef
import com.rmsr.myguard.data.mapper.ReversibleMapper
import com.rmsr.myguard.domain.entity.ScheduleId
import com.rmsr.myguard.domain.entity.Session
import com.rmsr.myguard.domain.entity.SessionId
import com.rmsr.myguard.domain.repository.SessionRepository
import com.rmsr.myguard.utils.RxSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val scanSessionDao: ScanSessionDao,
    private val sessionScheduleXRefDao: SessionScheduleDao,
    private val sessionMapper: ReversibleMapper<ScanSessionEntity, Session>,
    private val rxSchedulers: RxSchedulers,
) : SessionRepository {

    override fun initNewScanSession(): Single<SessionId> {
        val sessionEntity = ScanSessionEntity(
            id = 0,
            createdTime = 0,
            startTime = 0,
            requestStartTime = 0,
            requestEndTime = 0,
            requestsAvgTime = 0,
            endTime = 0,
            userRespond = false
        )
        return scanSessionDao.insert(sessionEntity)
            .map(::SessionId)
    }

    override fun updateSession(scanSession: Session): Completable {
        val scanSessionEntity = sessionMapper.mapReversed(scanSession)

        return scanSessionDao.update(scanSessionEntity)
    }

    override fun saveSessionScannedSchedules(
        sessionId: SessionId,
        schedulesIds: Set<ScheduleId>
    ): Completable {
        val xRefList = schedulesIds.map { scheduleId ->
            SessionScheduleXRef(sessionId = sessionId.value, scheduleId = scheduleId.value)
        }

        return sessionScheduleXRefDao.insertSessionScheduleRef(xRefList)
    }


    override fun getSession(sessionId: SessionId): Maybe<Session> {
        return scanSessionDao.getSession(sessionId = sessionId.value)
            .subscribeOn(rxSchedulers.io())
            .map { sessionMapper.map(it) }
    }

    override fun getSessions(sessionsIds: List<SessionId>): Maybe<List<Session>> {
        return scanSessionDao.getSessions(sessionsIds = sessionsIds.map { it.value })
            .subscribeOn(rxSchedulers.io())
            .map { sessionMapper.map(it) }
    }

    override fun getLastSession(): Maybe<Session> {
        return scanSessionDao.getLastSession()
            .subscribeOn(rxSchedulers.io())
            .map { sessionMapper.map(it) }
    }

    override fun markSessionUserResponded(sessionId: Long): Completable {
        return scanSessionDao.userRespondedToSession(sessionId = sessionId)
            .subscribeOn(rxSchedulers.io())
    }
}


