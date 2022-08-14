package com.rmsr.myguard.di

import com.rmsr.myguard.data.database.history_caching.HistoryDisabledStrategy
import com.rmsr.myguard.data.database.history_caching.HistoryEnabledStrategy
import javax.inject.Qualifier


/**
 * A Qualifier Used to make fields Injectable with [HistoryEnabledStrategy] class when needed.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EnabledCacheStrategy

/**
 * A Qualifier Used to make fields Injectable with [HistoryDisabledStrategy] class when needed.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DisabledCacheStrategy
