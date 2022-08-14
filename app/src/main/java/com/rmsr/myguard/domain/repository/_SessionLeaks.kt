package com.rmsr.myguard.domain.repository

import com.rmsr.myguard.domain.entity.Breach
import com.rmsr.myguard.domain.entity.SessionId
import io.reactivex.rxjava3.core.Maybe

@Deprecated("This data is specific to Domain layer needs and should computed and gathered there.")
interface _SessionLeaks {

    /**
     * Return session breaches that user didn't notified about.
     *
     * @param sessionId specify to get this session breaches, or `null` to get last session breaches.
     * @return UnNotified breaches of this session.
     */
    @Deprecated("")
    fun getSessionNewLeaks(sessionId: SessionId?): Maybe<List<Breach>>

    /**
     * Return all breaches found in a specific session.
     *
     * @param sessionId id of needed session.
     * @return all breaches of this session.
     */
    @Deprecated("")
    fun getSessionAllLeaks(sessionId: SessionId): Maybe<List<Breach>>
}