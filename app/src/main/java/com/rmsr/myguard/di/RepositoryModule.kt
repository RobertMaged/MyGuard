package com.rmsr.myguard.di

import com.rmsr.myguard.data.preferences.SettingsImpl
import com.rmsr.myguard.data.repository.*
import com.rmsr.myguard.domain.repository.*
import com.rmsr.myguard.utils.CoroutinesDispatchers
import com.rmsr.myguard.utils.RxSchedulers
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideBreachRepository(
        breachRepositoryImpl: BreachRepositoryImpl,
    ): BreachRepository

    @Binds
    @Singleton
    abstract fun providePasswordRepository(
        passwordRepositoryImpl: PasswordRepositoryImpl,
    ): PasswordRepository

    @Binds
    @Singleton
    abstract fun provideScheduleLeaksRepository(
        scanRecordRepositoryImpl: ScanRecordsRepositoryImpl,
    ): _ScheduleLeaks

    @Binds
    @Singleton
    abstract fun provideSessionLeaksRepository(
        scanRecordRepositoryImpl: ScanRecordsRepositoryImpl,
    ): _SessionLeaks

    @Binds
    @Singleton
    abstract fun provideScheduleRepository(
        scheduleRepositoryImpl: ScheduleRepositoryImpl,
    ): ScheduleWriteRepo

    @Binds
    @Singleton
    abstract fun provideScheduleRetrieveRepo(
        scheduleRepositoryImpl: ScheduleRepositoryImpl,
    ): ScheduleRetrieveRepo

    @Binds
    @Singleton
    abstract fun provideSessionRepository(
        sessionRepository: SessionRepositoryImpl,
    ): SessionRepository


    @Binds
    @Singleton
    abstract fun provideScanRecordRepository(
        ScanRecordRepositoryImpl: ScanRecordsRepositoryImpl,
    ): ScanRecordsRepository

    @Binds
    @Singleton
    abstract fun provideSettingsImpl(
        settingsImpl: SettingsImpl,
    ): Settings

    @Binds
    @Singleton
    abstract fun provideHistoryRepository(
        historyRepositoryImpl: _HistoryRepositoryImpl,
    ): _HistoryRepository


    @Binds
    @Singleton
    abstract fun provideMainRxSchedulers(
        mainRxSchedulers: MainRxSchedulers,
    ): RxSchedulers

    @Binds
    @Singleton
    abstract fun provideMainCoroutinesDispatchers(
        mainCoroutinesDispatchers: MainCoroutinesDispatchers,
    ): CoroutinesDispatchers

    @Binds
    abstract fun provideErrorHandle(
        errorHandleImpl: ErrorHandleImpl,
    ): ErrorHandler

    companion object {
        class MainRxSchedulers @Inject constructor() : RxSchedulers {
            override fun io(): Scheduler = Schedulers.io()
            override fun computation(): Scheduler = Schedulers.computation()
            override fun trampoline(): Scheduler = Schedulers.trampoline()
            override fun mainThread(): Scheduler = AndroidSchedulers.mainThread()
            override fun single(): Scheduler = Schedulers.single()
        }

        class MainCoroutinesDispatchers @Inject constructor() : CoroutinesDispatchers {
            override fun main(): CoroutineDispatcher = Dispatchers.Main

            override fun io(): CoroutineDispatcher = Dispatchers.IO

            override fun default(): CoroutineDispatcher = Dispatchers.Default
        }
    }
}