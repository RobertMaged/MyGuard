package com.rmsr.myguard.presentation.ui.schedules.mainfragment

import com.rmsr.myguard.domain.entity.Schedule
import com.rmsr.myguard.presentation.util.UserCommunicate

data class SchedulesUiState(
    val isLoadingSchedules: Boolean = false,
    val schedulesItems: List<SchedulesItemUiState> = emptyList(),
    val userMessages: List<UserCommunicate> = emptyList(),
    val totalFoundLeaksCount: Int = 0,
    val showDefaultScreen: Boolean = true,
)

data class SchedulesItemUiState(
    val schedule: Schedule,
    val newLeaksCount: Int = 0,
    val lastLeaks: String = "No Leaks", // List<_Breach> = emptyList(),
    val date: String,
    val isItemExpanded: Boolean = false,
) {

    val lastLeaksFormatted: String
        get() = lastLeaks

    val lastLeakDate: String
        get() = date
}
