package com.rmsr.myguard.domain.entity

import kotlin.reflect.KProperty


data class Session(
    val id: Long,

    val userRespond: Boolean,

    val sessionInfo: SessionInfo,
) {
    val wrappedId = SessionId(id)

    init {
        require(id > 0)
    }

    data class SessionInfo(
        val createdTime: Long,
        val startTime: Long,
        val requestStartTime: Long,
        val requestEndTime: Long,
        val requestsAvgTime: Long,
        val endTime: Long
    )
}

/**
 * Wrapper class for [Session.id] of type Long, to make less confusion when dealing with IDs.
 */
@JvmInline
value class SessionId(val value: Long) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Long = value
    operator fun component1(): Long = value
    fun toLong() = value
}