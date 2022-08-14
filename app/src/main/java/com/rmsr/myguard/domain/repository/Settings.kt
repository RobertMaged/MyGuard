package com.rmsr.myguard.domain.repository

import com.rmsr.myguard.domain.utils.ScanInterval

interface Settings {
    val isOnNewLeaksOnlyEnabled: Boolean

    var schedulesScanInterval: ScanInterval

    var isSchedulesEnabled: Boolean

    var isFirstAppRun: Boolean

    var appVersion: Int
}
