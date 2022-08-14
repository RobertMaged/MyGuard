package com.rmsr.myguard.domain.entity

data class ScanRecord(
    val identifiers: Identifiers,
    val recordInfo: RecordInfo = RecordInfo(),
) {
    data class Identifiers(
        val scheduleId: ScheduleId,
        val breachId: BreachId,
        val sessionId: SessionId,
    )

    data class RecordInfo(
        val userNotified: Boolean = false,
        val notifyTime: Long = 0,
        val userAcknowledged: Boolean = false,
        val acknowledgeTime: Long = 0,
    )

}