package com.rmsr.myguard.domain.usecase.settings

import com.rmsr.myguard.domain.repository.Settings
import com.rmsr.myguard.domain.utils.ScanInterval
import javax.inject.Inject

class SetPreferredScanIntervalUseCase @Inject constructor(
    private val settings: Settings
) {

    operator fun invoke(interval: ScanInterval) {
        settings.schedulesScanInterval = interval
    }

    operator fun invoke(intervalInDays: Int) {
        settings.schedulesScanInterval = ScanInterval.fromDays(intervalInDays)
    }

}