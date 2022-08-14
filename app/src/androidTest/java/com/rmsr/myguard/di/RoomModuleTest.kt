package com.rmsr.myguard.di

import android.content.Context
import androidx.room.Room
import com.rmsr.myguard.data.database.MyGuardDatabase
import com.rmsr.myguard.data.database.converters.MyGuardDbConverters
import com.rmsr.myguard.data.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RoomModule::class]
)
class RoomModuleTest {

//    @Provides
//    fun provideScheduleCheckDatabase(@ApplicationContext context: Context): MyGuardDatabase {
//        return Room.inMemoryDatabaseBuilder(context, MyGuardDatabase::class.java)
//            .addTypeConverter(BreachDbConverters())
//            .allowMainThreadQueries()
//            .build()
//    }
//
//    @Provides
//
//    fun provideSavedEmailsDao(scheduleDatabase: MyGuardDatabase): OldScheduleCheckDao {
//        return scheduleDatabase.scheduleCheckDao()
//    }
//
//    @Provides
//    fun provideBreachesDao(scheduleDatabase: MyGuardDatabase): BreachDao {
//        return scheduleDatabase.breachDao()
//    }
//
//    @Provides
//    fun provideRelationsDao(scheduleDatabase: MyGuardDatabase): RelationsDao {
//        return scheduleDatabase.relationsDao()
//    }


    @Provides
    fun provideNewVersionDB(@ApplicationContext context: Context): MyGuardDatabase {
        return Room.inMemoryDatabaseBuilder(context, MyGuardDatabase::class.java)
            .addTypeConverter(MyGuardDbConverters())
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    fun provideQueryDao(scheduleDatabase: MyGuardDatabase): QueryDao {
        return scheduleDatabase.queryDao()
    }

    @Provides
    fun provideBreachesDao(scheduleDatabase: MyGuardDatabase): BreachDao {
        return scheduleDatabase.breachDao()
    }

    @Provides
    fun provideSearchHistoryDao(scheduleDatabase: MyGuardDatabase): SearchHistoryDao {
        return scheduleDatabase.historyDao()
    }

    @Provides
    fun provideHistoryBreachDao(scheduleDatabase: MyGuardDatabase): HistoryBreachDao {
        return scheduleDatabase.historyBreachDao()
    }

    @Provides
    fun provideScheduleDao(scheduleDatabase: MyGuardDatabase): ScheduleDao {
        return scheduleDatabase.scheduleDao()
    }

    @Provides
    fun provideScanSessionDao(scheduleDatabase: MyGuardDatabase): ScanSessionDao {
        return scheduleDatabase.scanSessionDao()
    }

    @Provides
    fun provideScanRecordDao(scheduleDatabase: MyGuardDatabase): ScanRecordDao {
        return scheduleDatabase.scanRecordDao()
    }

    @Provides
    fun provideSessionScheduleDao(scheduleDatabase: MyGuardDatabase): SessionScheduleDao {
        return scheduleDatabase.sessionScheduleDao()
    }
}
