package com.rmsr.myguard.domain.utils


/**
 * Define whether to save searched queries to history not.
 */
enum class HistoryMode {
    /**
     * Apply user preferred history mode which is saved in settings.
     */
    DEFAULT,

    /**
     * Enable searched queries to be saved in history.
     */
    ENABLED,

    /**
     * Disable searched queries to be saved in history.
     */
    DISABLED
}