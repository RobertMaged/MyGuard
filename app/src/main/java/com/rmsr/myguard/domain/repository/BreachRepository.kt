package com.rmsr.myguard.domain.repository

import com.rmsr.myguard.domain.entity.Breach
import com.rmsr.myguard.domain.entity.BreachId
import com.rmsr.myguard.domain.entity.SearchQuery
import com.rmsr.myguard.domain.utils.HistoryMode
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.MaybeObserver

interface BreachRepository {

    /**
     * Get all leaks this [query] appears in.
     *
     * @param query [SearchQuery] to search for.
     * @param historyMode How history should deal with this searched [query].
     *
     * @return [MaybeObserver.onSuccess] with found [Breach]s,
     *  or [MaybeObserver.onComplete] if no leaks found for this [SearchQuery].
     *
     *  @see searchForLeaksIds
     *  @see HistoryMode
     */
    fun searchForLeaks(
        query: SearchQuery,
        historyMode: HistoryMode = HistoryMode.DEFAULT,
    ): Maybe<List<Breach>>

    /**
     * Get all leaks ids this [query] appears in.
     *
     * @param query [SearchQuery] to search for.
     * @param historyMode How history should deal with this searched [query].
     *
     * @return [MaybeObserver.onSuccess] with found [BreachId]s,
     *  or [MaybeObserver.onComplete] if no leaks found for this [SearchQuery].
     *
     *  @see searchForLeaks
     *  @see HistoryMode
     */
    fun searchForLeaksIds(
        query: SearchQuery,
        historyMode: HistoryMode = HistoryMode.DEFAULT,
    ): Maybe<List<BreachId>>

    fun getAllBreaches(): Maybe<List<Breach>>

    fun getBreachById(breachId: BreachId): Maybe<Breach>

    fun getBreachesByIds(breachesIds: List<BreachId>): Maybe<List<Breach>>

    fun getBreachesByIds(breachesIds: Iterable<Long>): Maybe<List<Breach>>

}

