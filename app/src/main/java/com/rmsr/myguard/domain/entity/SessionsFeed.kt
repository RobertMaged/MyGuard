package com.rmsr.myguard.domain.entity


data class SessionsFeed(

    /**
     * Represents all [Session]s which new leaks found for scanned [Schedule].
     */
    val leaksFoundSessions: Set<Session>,

    /**
     * Latest [Session] created with new leaks found for a [Schedule].
     *
     * Null if no scan happen to [Schedule] or its clean and have no leaks.
     */
    val lastLeaksFoundSession: Session? = leaksFoundSessions.maxByOrNull { it.sessionInfo.createdTime },

//    @Suppress("not implemented yet.")
//    val noLeaksFoundSessions: Set<Session>,
//    val lastSession: Session? ,
) {

    companion object {

        val EMPTY = SessionsFeed(emptySet(), null)
    }
}
