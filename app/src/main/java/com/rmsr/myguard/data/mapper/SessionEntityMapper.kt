package com.rmsr.myguard.data.mapper

import com.rmsr.myguard.data.database.entity.ScanSessionEntity
import com.rmsr.myguard.domain.entity.Session
import javax.inject.Inject

class SessionEntityMapper @Inject constructor() : ReversibleMapper<ScanSessionEntity, Session> {
    override fun map(input: ScanSessionEntity): Session {
        val info = Session.SessionInfo(
            createdTime = input.createdTime,
            startTime = input.startTime,
            requestStartTime = input.requestStartTime,
            requestEndTime = input.requestEndTime,
            requestsAvgTime = input.requestsAvgTime,
            endTime = input.endTime
        )

        return Session(id = input.id, userRespond = input.userRespond, sessionInfo = info)
    }

    override fun mapReversed(input: Session): ScanSessionEntity {
        val info = input.sessionInfo
        return ScanSessionEntity(
            id = input.id,
            createdTime = info.createdTime,
            startTime = info.startTime,
            requestStartTime = info.requestStartTime,
            requestEndTime = info.requestEndTime,
            requestsAvgTime = info.requestsAvgTime, endTime = info.endTime,
            userRespond = input.userRespond
        )
    }
}