package com.rmsr.myguard.data.preferences

import com.rmsr.myguard.domain.repository.Settings
import com.rmsr.myguard.domain.utils.ScanInterval
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class SettingsImpl @Inject constructor(
    private val setting: SettingsLocalDataSource
) : Settings {

    override val isOnNewLeaksOnlyEnabled: Boolean
        get() = setting.onNewLeaksOnlyNotify

    override var schedulesScanInterval: ScanInterval
        get() = ScanInterval.fromDays(setting.schedulesScanIntervalDays)
        set(value) {
            setting.schedulesScanIntervalDays = value.inDays
        }

    override var isSchedulesEnabled: Boolean
        get() = setting.schedulesEnabled
        set(value) {
            setting.schedulesEnabled = value
        }

    override var isFirstAppRun: Boolean
        get() = setting.firstAppRun
        set(value) {
            setting.firstAppRun = value
        }

    open val isIncognitoEnabled: Boolean
        get() = setting.incognitoEnabled

    override var appVersion: Int
        get() = setting.appInstalledVersion
        set(value) {
            setting.appInstalledVersion = value
        }
}