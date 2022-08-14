package com.rmsr.myguard.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rmsr.myguard.data.database.converters.MyGuardDbConverters
import com.rmsr.myguard.data.database.dao.*
import com.rmsr.myguard.data.database.entity.*
import com.rmsr.myguard.data.database.entity.relations.HistoryBreachXRef
import com.rmsr.myguard.data.database.entity.relations.SessionScheduleXRef

@TypeConverters(MyGuardDbConverters::class)
@Database(
    entities = [
        QueryEntity::class,
        BreachEntity::class,
        SearchHistoryEntity::class,
        HistoryBreachXRef::class,
        ScheduleEntity::class,
        ScanSessionEntity::class,
        SessionScheduleXRef::class,
        ScheduleScanRecordEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class MyGuardDatabase : RoomDatabase() {
    abstract fun queryDao(): QueryDao

    abstract fun historyDao(): SearchHistoryDao

    abstract fun historyBreachDao(): HistoryBreachDao

    abstract fun breachDao(): BreachDao

    abstract fun scheduleDao(): ScheduleDao

    abstract fun sessionScheduleDao(): SessionScheduleDao

    abstract fun scanSessionDao(): ScanSessionDao

    abstract fun scanRecordDao(): ScanRecordDao
}