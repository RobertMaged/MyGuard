package com.rmsr.myguard.data.database.entity

import androidx.room.*

@Entity(
    tableName = ScheduleEntity.SCHEMA.TABLE_NAME,
    indices = [Index(value = [ScheduleEntity.SCHEMA.QUERY_ID], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = QueryEntity::class,
            parentColumns = [QueryEntity.SCHEMA.ID],
            childColumns = [ScheduleEntity.SCHEMA.QUERY_ID],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = SCHEMA.ID)
    val id: Long = 0,

    @ColumnInfo(name = SCHEMA.QUERY_ID)
    val queryId: Long,
    @ColumnInfo(name = SCHEMA.IS_MUTED, defaultValue = "FALSE")
    val isMuted: Boolean = false,
    @ColumnInfo(name = SCHEMA.CREATED)
    val createdTime: Long,
//    @ColumnInfo(name = SCHEMA.DELETED_AT, defaultValue = "NULL")
//    val deletedAt : Long?
) {

    object SCHEMA {
        const val TABLE_NAME = "schedules"
        private const val PREFIX = "sch_"

        const val ID = "_scheduleId"
        const val QUERY_ID = "${PREFIX}query_id"
        const val IS_MUTED = "${PREFIX}isMuted"
        const val CREATED = "${PREFIX}created"
        const val DELETED_AT = "${PREFIX}deleted_at"
    }
}