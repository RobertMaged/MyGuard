package com.rmsr.myguard.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.rmsr.myguard.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SettingsLocalDataSource private constructor(private val preferences: SharedPreferences) {

    @Inject
    constructor(@ApplicationContext context: Context) :
            this(PreferenceManager.getDefaultSharedPreferences(context))

    companion object {
        private const val SCHEDULES_SCAN_INTERVAL = "Schedules_Scan_Interval_Type"
        private const val INCOGNITO_MODE = "INCOGNITO_MODE"
        private const val ON_NEW_LEAKS_NOTIFY = "NOTIFY_ON_NEW_LEAKS_ONLY"
        private const val SCHEDULES_SCAN_ENABLED = "SCHEDULES_CHECK_ENABLED"
        private const val FIRST_TIME_APP_RUN = "FIRST_TIME"
        private const val APP_INSTALLED_VERSION = "APP_VERSION"
    }

    var schedulesScanIntervalDays: Int
        get() = preferences.getInt(SCHEDULES_SCAN_INTERVAL, 4)
        set(value) = preferences.edit { putInt(SCHEDULES_SCAN_INTERVAL, value) }

    var incognitoEnabled
        get() = preferences.getBoolean(INCOGNITO_MODE, false)
        set(value) = preferences.edit { putBoolean(INCOGNITO_MODE, value) }

    var onNewLeaksOnlyNotify
        get() = preferences.getBoolean(ON_NEW_LEAKS_NOTIFY, false)
        set(value) = preferences.edit { putBoolean(ON_NEW_LEAKS_NOTIFY, value) }

    var schedulesEnabled
        get() = preferences.getBoolean(SCHEDULES_SCAN_ENABLED, false)
        set(value) = preferences.edit { putBoolean(SCHEDULES_SCAN_ENABLED, value) }

    var firstAppRun
        get() = preferences.getBoolean(FIRST_TIME_APP_RUN, true)
        set(_) = preferences.edit { putBoolean(FIRST_TIME_APP_RUN, false) }

    var appInstalledVersion
        get() = preferences.getInt(APP_INSTALLED_VERSION, 1)
        set(_) = preferences.edit { putInt(APP_INSTALLED_VERSION, BuildConfig.VERSION_CODE) }
}