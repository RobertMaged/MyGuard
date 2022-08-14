package com.rmsr.myguard.data.database.entity.relations

import androidx.room.Embedded
import com.rmsr.myguard.data.database.entity.QueryEntity
import com.rmsr.myguard.data.database.entity.SearchHistoryEntity

data class HistoryWithQuery(
    @Embedded val historyEntity: SearchHistoryEntity,
    @Embedded val queryEntity: QueryEntity
)
