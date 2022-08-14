package com.rmsr.myguard.domain.entity

/**
 * A container of [Breach]s, partitioned into different categories.
 */
data class LeaksFeed(
    /**
     * Neither Notified nor Acknowledged [Breach]s for a specific [Schedule].
     *
     * This should be empty most time unless user has disabled notifications.
     */
    val fresh: Set<Breach>,

    /**
     * Notified but not Acknowledged [Breach]s for a specific [Schedule].
     */
    val notifiedOnly: Set<Breach>,

    /**
     * Notified and Acknowledged [Breach]s for a specific [Schedule].
     */
    val notifiedAndAcknowledged: Set<Breach>,

    /**
     * Leaks found in last [Session] created for a specific [Schedule].
     */
    val lastFoundLeaks: Set<Breach>,
) {

    val allLeaks: Set<Breach>
        get() = fresh + notifiedOnly + notifiedAndAcknowledged

    val notAcknowledged: Set<Breach>
        get() = fresh + notifiedOnly

    companion object {
        val EMPTY = LeaksFeed(emptySet(), emptySet(), emptySet(), emptySet())
    }
}

val LeaksFeed.formattedLastLeaks: String
    get() = lastFoundLeaks.takeIf { it.isNotEmpty() }?.joinToString(limit = 4) { it.title }
        ?: "No Leaks"