package com.rmsr.myguard.domain.entity

import java.time.LocalDate
import kotlin.reflect.KProperty


data class Breach(
    val id: Long,

    val title: String,

    val metadata: Metadata,

    val leakInfo: LeakInfo,
) {

    val wrappedId = BreachId(id)

    data class Metadata(
        val logoUrl: String,
        val domain: String,
        val createdTime: Long,
    )

    data class LeakInfo(
        val description: String,
        val pwnCount: Int,

        val discoveredDate: LocalDate,
        /**
         * What has pwned. ex. "USERNAME" "PASSWORD".
         */
        val compromisedData: List<String>,
    )

    companion object {
        val EMPTY by lazy {
            Breach(
                id = 0,
                title = "",
                metadata = Metadata(logoUrl = "", domain = "", createdTime = 0),
                leakInfo = LeakInfo(
                    description = "",
                    pwnCount = 0,
                    discoveredDate = LocalDate.MIN,
                    compromisedData = emptyList()
                )
            )
        }
    }
}

/**
 * Wrapper class for [Breach.id] of type Long, to make less confusion when dealing with IDs.
 */
@JvmInline
value class BreachId(val value: Long) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Long = value
    operator fun component1(): Long = value
    fun toLong() = value
}
