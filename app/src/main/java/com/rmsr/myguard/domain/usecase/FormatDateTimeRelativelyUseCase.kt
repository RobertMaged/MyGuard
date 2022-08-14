package com.rmsr.myguard.domain.usecase

import android.text.format.DateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

class FormatDateTimeRelativelyUseCase private constructor() {

    companion object {
        @JvmStatic
        operator fun invoke(date: Date): String {
            val thenCal: Calendar = GregorianCalendar().apply { timeInMillis = date.time }
            val nowCal: Calendar =
                GregorianCalendar().apply { timeInMillis = System.currentTimeMillis() }

            val inFormat = when {
                thenCal[Calendar.YEAR] != nowCal[Calendar.YEAR] -> "dd/mm/yyyy"
                thenCal[Calendar.MONTH] != nowCal[Calendar.MONTH] -> "MMM d"
                thenCal[Calendar.DAY_OF_MONTH] == nowCal[Calendar.DAY_OF_MONTH] -> "h:mm a"
                thenCal[Calendar.DAY_OF_MONTH] == nowCal[Calendar.DAY_OF_MONTH] - 1 -> return "Yesterday"
                thenCal[Calendar.WEEK_OF_MONTH] == nowCal[Calendar.WEEK_OF_MONTH] -> "EEE"
                else -> "MMM d"
            }

            return DateFormat.format(inFormat, thenCal).toString()
        }

        @Suppress("Experimental", "not tested yet")
        @JvmStatic
        operator fun invoke(date: LocalDateTime): String {

            val thenCal = date
            val nowCal = LocalDateTime.now(ZoneId.systemDefault())
            val weakOfMonth by lazy(LazyThreadSafetyMode.NONE) {
                WeekFields.of(Locale.getDefault()).weekOfMonth()
            }

            val inFormat = when {
                thenCal.year != nowCal.year -> "dd/mm/yyyy"
                thenCal.monthValue != nowCal.monthValue -> "MMM d"
                thenCal.dayOfMonth == nowCal.dayOfMonth -> "h:mm a"
                thenCal.dayOfMonth == nowCal.dayOfMonth - 1 -> return "Yesterday"
                thenCal[weakOfMonth] == nowCal[weakOfMonth] -> "EEE"
                else -> "MMM d"
            }

            return thenCal.format(DateTimeFormatter.ofPattern(inFormat, Locale.getDefault()))
        }
    }
}