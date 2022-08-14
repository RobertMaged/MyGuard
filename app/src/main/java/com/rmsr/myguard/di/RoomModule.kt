package com.rmsr.myguard.di

import android.content.Context
import androidx.room.Room
import com.rmsr.myguard.BuildConfig
import com.rmsr.myguard.data.database.MyGuardDatabase
import com.rmsr.myguard.data.database.converters.MyGuardDbConverters
import com.rmsr.myguard.data.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideNewVersionDB(@ApplicationContext context: Context): MyGuardDatabase {

        return Room.databaseBuilder(context, MyGuardDatabase::class.java, "myguard_db")
            .addTypeConverter(MyGuardDbConverters())
            .apply {
                if (BuildConfig.DEBUG)
                    createFromAsset("myguard_db.db")
            }
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideQueryDao(scheduleDatabase: MyGuardDatabase): QueryDao {
        return scheduleDatabase.queryDao()
    }

    @Provides
    @Singleton
    fun provideBreachesDao(scheduleDatabase: MyGuardDatabase): BreachDao {
        return scheduleDatabase.breachDao()
    }

    @Provides
    @Singleton
    fun provideSearchHistoryDao(scheduleDatabase: MyGuardDatabase): SearchHistoryDao {
        return scheduleDatabase.historyDao()
    }

    @Provides
    @Singleton
    fun provideHistoryBreachDao(scheduleDatabase: MyGuardDatabase): HistoryBreachDao {
        return scheduleDatabase.historyBreachDao()
    }

    @Provides
    @Singleton
    fun provideScheduleDao(scheduleDatabase: MyGuardDatabase): ScheduleDao {
        return scheduleDatabase.scheduleDao()
    }

    @Provides
    @Singleton
    fun provideScanSessionDao(scheduleDatabase: MyGuardDatabase): ScanSessionDao {
        return scheduleDatabase.scanSessionDao()
    }

    @Provides
    @Singleton
    fun provideScanRecordDao(scheduleDatabase: MyGuardDatabase): ScanRecordDao {
        return scheduleDatabase.scanRecordDao()
    }

    @Provides
    @Singleton
    fun provideSessionScheduleDao(scheduleDatabase: MyGuardDatabase): SessionScheduleDao {
        return scheduleDatabase.sessionScheduleDao()
    }

}