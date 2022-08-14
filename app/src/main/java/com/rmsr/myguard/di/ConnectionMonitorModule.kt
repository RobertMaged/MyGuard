package com.rmsr.myguard.di

import com.rmsr.myguard.data.utils.NetworkConnectionMonitoring
import com.rmsr.myguard.domain.utils.ConnectionMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface ConnectionMonitorModule {

    @Binds
    @Singleton
    fun bindNetworkMonitor(
        networkConnectionMonitoring: NetworkConnectionMonitoring
    ): ConnectionMonitor

}