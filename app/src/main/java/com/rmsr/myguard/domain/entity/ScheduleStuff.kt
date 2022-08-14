package com.rmsr.myguard.domain.entity

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*


data class ScheduleStuff(
    val schedule: Schedule,
    val leaks: LeaksFeed,
    val sessions: SessionsFeed,
    val scanRecords: List<ScanRecord>,
) {
    val lastMassageTime: Date
        get() = Date(
            sessions.lastLeaksFoundSession?.sessionInfo?.createdTime
                ?: schedule.createdTime
        )

    val lastMassageTime2: LocalDateTime = LocalDateTime.ofEpochSecond(
        sessions.lastLeaksFoundSession?.sessionInfo?.createdTime
            ?: schedule.createdTime,
        0,
        ZoneOffset.UTC
    )
}
