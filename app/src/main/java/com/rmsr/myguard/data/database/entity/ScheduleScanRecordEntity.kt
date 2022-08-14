package com.rmsr.myguard.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = ScheduleScanRecordEntity.SCHEMA.TABLE_NAME,
    primaryKeys = [ScheduleScanRecordEntity.SCHEMA.SCHEDULE_ID, ScheduleScanRecordEntity.SCHEMA.BREACH_ID],
    indices = [Index(value = [ScheduleScanRecordEntity.SCHEMA.SESSION_ID])],
    foreignKeys = [
        ForeignKey(
            entity = ScanSessionEntity::class,
            parentColumns = [ScanSessionEntity.SCHEMA.ID],
            childColumns = [ScheduleScanRecordEntity.SCHEMA.SESSION_ID],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ScheduleEntity::class,
            parentColumns = [ScheduleEntity.SCHEMA.ID],
            childColumns = [ScheduleScanRecordEntity.SCHEMA.SCHEDULE_ID],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class ScheduleScanRecordEntity(
    @ColumnInfo(name = SCHEMA.SCHEDULE_ID)
    val scheduleId: Long,
    @ColumnInfo(name = SCHEMA.BREACH_ID)
    val breachId: Long,

    @ColumnInfo(name = SCHEMA.SESSION_ID)
    val sessionId: Long?, //Todo try to cancel null

    @ColumnInfo(name = SCHEMA.USER_NOTIFIED)
    val userNotified: Boolean,
    @ColumnInfo(name = SCHEMA.NOTIFY_TIME)
    val notifyTime: Long,
    @ColumnInfo(name = SCHEMA.USER_ACKNOWLEDGED)
    val userAcknowledged: Boolean,
    @ColumnInfo(name = SCHEMA.ACKNOWLEDGE_TIME)
    val acknowledgeTime: Long,
) {
    object SCHEMA {
        const val TABLE_NAME = "schedule_scan_record"
        private const val PREFIX = "rec_"

        const val SCHEDULE_ID = "${PREFIX}schedule_id"
        const val BREACH_ID = "${PREFIX}breach_id"
        const val SESSION_ID = "${PREFIX}session_id"
        const val USER_NOTIFIED = "${PREFIX}notified"
        const val NOTIFY_TIME = "${PREFIX}notifyTime"
        const val USER_ACKNOWLEDGED = "${PREFIX}acknowledged"
        const val ACKNOWLEDGE_TIME = "${PREFIX}acknowledgeTime"
    }
}
