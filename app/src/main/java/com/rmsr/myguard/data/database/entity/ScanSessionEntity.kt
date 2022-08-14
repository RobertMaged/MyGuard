package com.rmsr.myguard.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//scan record

/*

+ reqState: Enum
+ reqResponse: Enum

+ foundNewLeaks: bool

 */
@Entity(tableName = ScanSessionEntity.SCHEMA.TABLE_NAME)
data class ScanSessionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = SCHEMA.ID)
    val id: Long = 0,

    @ColumnInfo(name = SCHEMA.CREATED)
    val createdTime: Long,
    @ColumnInfo(name = SCHEMA.START_TIME)
    val startTime: Long,
    @ColumnInfo(name = SCHEMA.REQUEST_START_TIME)
    val requestStartTime: Long,
    @ColumnInfo(name = SCHEMA.REQUEST_END_TIME)
    val requestEndTime: Long,
    @ColumnInfo(name = SCHEMA.REQUESTS_AVG_TIME)
    val requestsAvgTime: Long,
    @ColumnInfo(name = SCHEMA.END_TIME)
    val endTime: Long,

    @ColumnInfo(name = SCHEMA.USER_RESPOND)
    val userRespond: Boolean,
) {
    constructor() : this(
        id = 0,
        createdTime = 0,
        startTime = 0,
        requestStartTime = 0,
        requestEndTime = 0,
        requestsAvgTime = 0,
        endTime = 0,
        userRespond = false
    )

    object SCHEMA {
        const val TABLE_NAME = "scan_sessions"
        private const val PREFIX = "scn_"

        const val ID = "_sessionId"
        const val CREATED = "${PREFIX}created"
        const val START_TIME = "${PREFIX}started"
        const val REQUEST_START_TIME = "${PREFIX}reqStarted"
        const val REQUEST_END_TIME = "${PREFIX}reqEnded"
        const val REQUESTS_AVG_TIME = "${PREFIX}requestsAvg"
        const val END_TIME = "${PREFIX}ended"
        const val USER_RESPOND = "${PREFIX}responded"
    }
}
