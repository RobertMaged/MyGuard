package com.rmsr.myguard.domain.usecase.settings

import com.rmsr.myguard.domain.repository.ScheduleRetrieveRepo
import com.rmsr.myguard.domain.repository.Settings
import javax.inject.Inject

@Suppress("Not used or tested")
class TurnSchedulesScanOnUseCase @Inject constructor(
    private val settings: Settings,
    private val scheduleRepository: ScheduleRetrieveRepo
) {

    operator fun invoke(): Boolean = when {
        settings.isSchedulesEnabled -> false

        else -> {
            settings.isSchedulesEnabled = true
            true
        }
    }

    operator fun invoke(function: () -> Boolean): Boolean {
        if (settings.isSchedulesEnabled) return false

        val isSuccessful = function()
        if (isSuccessful.not()) return false

        settings.isSchedulesEnabled = true
        return true
    }


}

