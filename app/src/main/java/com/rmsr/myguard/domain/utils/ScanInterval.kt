package com.rmsr.myguard.domain.utils

enum class ScanInterval(val inDays: Int) {
    DAILY(1),
    FOUR_DAYS(4),
    WEEKLY(7),
    TWO_WEEKS(14),
    MONTHLY(30);

    companion object {
        @JvmStatic
        fun fromDays(days: Int): ScanInterval = when (days) {
            1 -> DAILY
            4 -> FOUR_DAYS
            7 -> WEEKLY
            14 -> TWO_WEEKS
            30 -> MONTHLY
            else -> throw IllegalArgumentException("Not Supported Interval Days.")
        }
    }
}