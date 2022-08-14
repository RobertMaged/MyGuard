package com.rmsr.myguard.data.database.history_caching

import com.rmsr.myguard.data.preferences.SettingsImpl
import com.rmsr.myguard.domain.utils.HistoryMode
import javax.inject.Inject

class HistoryCachingStrategyFactory @Inject constructor(
    private val settings: SettingsImpl,
) {

    @Inject
    lateinit var enabledCache: dagger.Lazy<HistoryEnabledStrategy>

    @Inject
    lateinit var disabledCache: dagger.Lazy<HistoryDisabledStrategy>

    fun fromMode(historyMode: HistoryMode): HistoryCachingStrategy = when (historyMode) {
        HistoryMode.DEFAULT -> if (settings.isIncognitoEnabled) disabledCache.get() else enabledCache.get()
        HistoryMode.ENABLED -> enabledCache.get()
        HistoryMode.DISABLED -> disabledCache.get()
    }
}