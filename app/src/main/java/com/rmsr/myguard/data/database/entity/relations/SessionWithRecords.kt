package com.rmsr.myguard.data.database.entity.relations

import com.rmsr.myguard.data.database.entity.ScanSessionEntity
import com.rmsr.myguard.data.database.entity.ScheduleScanRecordEntity

data class SessionWithRecords(
    val sessionEntity: ScanSessionEntity,
    val recordsEntity: List<ScheduleScanRecordEntity>
)
