package com.rmsr.myguard.data.database.entity.relations

import androidx.room.Embedded
import com.rmsr.myguard.data.database.entity.QueryEntity
import com.rmsr.myguard.data.database.entity.ScheduleEntity

data class ScheduleWithQuery(
    @Embedded val scheduleEntity: ScheduleEntity,

    //@Relation(parentColumn = "query_id", entityColumn = "_queryId")
    @Embedded val queryEntity: QueryEntity
)