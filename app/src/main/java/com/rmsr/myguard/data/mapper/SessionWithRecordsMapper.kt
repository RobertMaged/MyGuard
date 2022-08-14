package com.rmsr.myguard.data.mapper

import com.rmsr.myguard.data.database.entity.ScanSessionEntity
import com.rmsr.myguard.data.database.entity.ScheduleScanRecordEntity
import com.rmsr.myguard.data.database.entity.relations.SessionWithRecords
import com.rmsr.myguard.domain.entity.*
import javax.inject.Inject

@Suppress("Not used.", "Not tested yet.")
class SessionWithRecordsMapper @Inject constructor() :
    ReversibleMapper<SessionWithRecords, Session> {


    override fun map(input: SessionWithRecords): Session {
        val info = input.sessionEntity.let {
            Session.SessionInfo(
                it.createdTime,
                it.startTime,
                it.requestStartTime,
                it.requestEndTime,
                it.requestsAvgTime,
                it.endTime
            )
        }

        val records = input.recordsEntity.map { entity ->
            ScanRecord(
                identifiers = ScanRecord.Identifiers(
                    scheduleId = ScheduleId(entity.scheduleId),
                    breachId = BreachId(entity.breachId),
                    //check: may make error when retrieve correctedData.
                    sessionId = SessionId(entity.sessionId ?: -1)
                ),
                recordInfo = ScanRecord.RecordInfo(
                    userNotified = entity.userNotified,
                    notifyTime = entity.notifyTime,
                    userAcknowledged = entity.userAcknowledged,
                    acknowledgeTime = entity.acknowledgeTime
                )
            )
        }

        return Session(
            input.sessionEntity.id,
            userRespond = input.sessionEntity.userRespond,
            sessionInfo = info
        )
    }


    override fun mapReversed(input: Session): SessionWithRecords {
        val info = input.sessionInfo ?: Session.SessionInfo(0, 0, 0, 0, 0, 0)
        val sessionEntity = ScanSessionEntity(
            id = input.id,
            createdTime = info.createdTime,
            startTime = info.startTime,
            requestStartTime = info.requestStartTime,
            requestEndTime = info.requestEndTime,
            requestsAvgTime = info.requestsAvgTime, endTime = info.endTime,
            userRespond = input.userRespond
        )

        throw NotImplementedError("Not Right until container class in DOMAIN LAYER created.")
//        val records = convertRecordsToEntityList(input.id, input.scanResult)
//
//        return SessionWithRecords(sessionEntity = sessionEntity, recordsEntity = records)
    }

    private fun convertRecordsToEntityList(
        sessionId: Long,
        scanRecords: List<ScanRecord>,
    ): List<ScheduleScanRecordEntity> = scanRecords.map { record ->
        ScheduleScanRecordEntity(
            scheduleId = record.identifiers.scheduleId.value,
            breachId = record.identifiers.breachId.value,
            sessionId = sessionId,
            userNotified = record.recordInfo.userNotified,
            notifyTime = record.recordInfo.notifyTime,
            userAcknowledged = record.recordInfo.userAcknowledged,
            acknowledgeTime = record.recordInfo.acknowledgeTime
        )
    }

}