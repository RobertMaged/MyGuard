package com.rmsr.myguard.presentation.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.rmsr.myguard.domain.usecase.ObserveConnectionStateUseCase
import com.rmsr.myguard.domain.usecase.settings.InitAppFirstTimeAfterUpdateUseCase
import com.rmsr.myguard.domain.usecase.settings.InitAppFirstTimeEverUseCase
import com.rmsr.myguard.domain.utils.NetworkStatus
import com.rmsr.myguard.presentation.util.AppNotificationsChannelsManager
import com.rmsr.myguard.presentation.workers.ScheduleWorker
import com.rmsr.myguard.utils.MyLog
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.rx3.asFlow
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    connectionMonitor: ObserveConnectionStateUseCase,
    @ApplicationContext context: Context,
    private val firstTimeUseCase: InitAppFirstTimeEverUseCase,
    private val firstTimeAfterUpdateUseCase: InitAppFirstTimeAfterUpdateUseCase,
    private val appNotificationsManager: dagger.Lazy<AppNotificationsChannelsManager>
) : ViewModel() {
    val connectionState: StateFlow<NetworkStatus> = connectionMonitor.invoke()
        .asFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            connectionMonitor.stateValue
        )

    init {
        appFirstTimeInit(context)
    }

    private fun appFirstTimeInit(context: Context) {
        val info = WorkManager.getInstance(context).getWorkInfosForUniqueWork("periodicLoopWork")
        MyLog.d(TAG, info.get().toString())

        firstTimeUseCase.invoke {
            MyLog.d(TAG, "initializing First time")

            ScheduleWorker.enqueuePeriodicWorkRequest(WorkManager.getInstance(context))

            return@invoke true
        }

        firstTimeAfterUpdateUseCase.invoke {
            MyLog.d(TAG, "initializing after updates")

            appNotificationsManager.get().initAll(context)

            return@invoke true
        }
    }


    companion object {
        private const val TAG = "Rob_MainViewModel"
    }
}