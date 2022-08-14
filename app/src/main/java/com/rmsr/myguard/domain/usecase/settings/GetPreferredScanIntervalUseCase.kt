package com.rmsr.myguard.domain.usecase.settings

import com.rmsr.myguard.domain.repository.Settings
import com.rmsr.myguard.domain.utils.ScanInterval
import javax.inject.Inject

class GetPreferredScanIntervalUseCase @Inject constructor(
    private val settings: Settings
) {

    operator fun invoke(): ScanInterval {
        return settings.schedulesScanInterval
    }

}