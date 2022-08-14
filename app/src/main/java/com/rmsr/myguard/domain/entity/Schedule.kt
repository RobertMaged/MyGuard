package com.rmsr.myguard.domain.entity

import kotlin.reflect.KProperty

data class Schedule(

    val id: Long = 0,
    val isMuted: Boolean = false,
    val createdTime: Long = 0,
    val searchQuery: SearchQuery,

    ) {

    val wrappedId = ScheduleId(id)
}

/**
 * Wrapper class for [Schedule.id] of type Long, to make less confusion when dealing with IDs.
 */
@JvmInline
value class ScheduleId(val value: Long) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Long = value
    operator fun component1(): Long = value
    fun toLong() = value
}