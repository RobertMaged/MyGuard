package com.rmsr.myguard.di

import com.rmsr.myguard.data.database.entity.BreachEntity
import com.rmsr.myguard.data.database.entity.QueryEntity
import com.rmsr.myguard.data.database.entity.ScanSessionEntity
import com.rmsr.myguard.data.database.entity.ScheduleScanRecordEntity
import com.rmsr.myguard.data.database.entity.relations.HistoryWithQuery
import com.rmsr.myguard.data.database.entity.relations.ScheduleWithQuery
import com.rmsr.myguard.data.mapper.*
import com.rmsr.myguard.data.remote.pojo_response.BreachResponse
import com.rmsr.myguard.domain.entity.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface MappersModule {


    @Binds
    fun provideBreachEntityMapper(
        breachEntityMapper: BreachEntityMapper,
    ): ReversibleMapper<BreachEntity, Breach>

    @Binds
    fun provideBreachResponseMapper(
        breachResponseMapper: BreachResponseMapper,
    ): Mapper<BreachResponse, BreachEntity>

    @Binds
    fun provideScheduleWithQueryMapper(
        scheduleWithQueryMapper: ScheduleWithQueryMapper,
    ): ReversibleMapper<ScheduleWithQuery, Schedule>

    @Binds
    fun provideQueryEntityMapper(
        queryEntityMapper: QueryEntityMapper,
    ): ReversibleMapper<QueryEntity, SearchQuery>


    @Binds
    fun provideRecordEntityMapper(
        recordEntityMapper: RecordEntityMapper,
    ): ReversibleMapper<ScheduleScanRecordEntity, ScanRecord>


    @Binds
    fun provideSessionEntityMapper(
        sessionEntityMapper: SessionEntityMapper,
    ): ReversibleMapper<ScanSessionEntity, Session>

    @Binds
    fun provideHistoryWithQueryMapper(
        historyWithQueryMapper: HistoryWithQueryMapper
    ): Mapper<HistoryWithQuery, _History>
}