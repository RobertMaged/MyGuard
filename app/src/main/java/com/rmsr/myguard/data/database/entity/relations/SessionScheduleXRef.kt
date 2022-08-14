package com.rmsr.myguard.data.database.entity.relations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.rmsr.myguard.data.database.entity.ScanSessionEntity
import com.rmsr.myguard.data.database.entity.ScheduleEntity

@Entity(
    tableName = SessionScheduleXRef.SCHEMA.TABLE_NAME,
    primaryKeys = [SessionScheduleXRef.SCHEMA.SESSION_ID, SessionScheduleXRef.SCHEMA.SCHEDULE_ID],
    foreignKeys = [
        ForeignKey(
            entity = ScanSessionEntity::class,
            parentColumns = [ScanSessionEntity.SCHEMA.ID],
            childColumns = [SessionScheduleXRef.SCHEMA.SESSION_ID],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ScheduleEntity::class,
            parentColumns = [ScheduleEntity.SCHEMA.ID],
            childColumns = [SessionScheduleXRef.SCHEMA.SCHEDULE_ID],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SessionScheduleXRef(
    @ColumnInfo(name = SCHEMA.SESSION_ID)
    val sessionId: Long,

    @ColumnInfo(name = SCHEMA.SCHEDULE_ID)
    val scheduleId: Long
) {
    object SCHEMA {
        const val TABLE_NAME = "scan_schedule_xref"
        private const val PREFIX = "scn_x_sch__"

        const val SESSION_ID = "${PREFIX}session_id"
        const val SCHEDULE_ID = "${PREFIX}schedule_id"
    }
}
