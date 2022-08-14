package com.rmsr.myguard.data.mapper

import com.rmsr.myguard.data.database.entity.ScheduleScanRecordEntity
import com.rmsr.myguard.domain.entity.BreachId
import com.rmsr.myguard.domain.entity.ScanRecord
import com.rmsr.myguard.domain.entity.ScheduleId
import com.rmsr.myguard.domain.entity.SessionId
import javax.inject.Inject

class RecordEntityMapper @Inject constructor() :
    ReversibleMapper<ScheduleScanRecordEntity, ScanRecord> {
    override fun map(input: ScheduleScanRecordEntity): ScanRecord {
        val identifiers = ScanRecord.Identifiers(
            scheduleId = ScheduleId(input.scheduleId),
            breachId = BreachId(input.breachId),
            sessionId = SessionId(input.sessionId!!)
        )

        val info = ScanRecord.RecordInfo(
            userNotified = input.userNotified,
            notifyTime = input.notifyTime,
            userAcknowledged = input.userAcknowledged,
            acknowledgeTime = input.acknowledgeTime
        )
        return ScanRecord(identifiers = identifiers, recordInfo = info)
    }

    override fun mapReversed(input: ScanRecord): ScheduleScanRecordEntity {
        return ScheduleScanRecordEntity(
            scheduleId = input.identifiers.scheduleId.value,
            breachId = input.identifiers.breachId.value,
            sessionId = input.identifiers.sessionId.value,
            userNotified = input.recordInfo.userNotified,
            notifyTime = input.recordInfo.notifyTime,
            userAcknowledged = input.recordInfo.userAcknowledged,
            acknowledgeTime = input.recordInfo.acknowledgeTime
        )
    }
}