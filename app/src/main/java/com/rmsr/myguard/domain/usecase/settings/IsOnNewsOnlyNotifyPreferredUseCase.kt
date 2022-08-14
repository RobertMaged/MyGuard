package com.rmsr.myguard.domain.usecase.settings

import com.rmsr.myguard.domain.repository.Settings
import javax.inject.Inject

class IsOnNewsOnlyNotifyPreferredUseCase @Inject constructor(
    private val settings: Settings,
) {

    operator fun invoke(): Boolean = settings.isOnNewLeaksOnlyEnabled

}
