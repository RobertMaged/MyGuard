package com.rmsr.myguard.domain.repository

import com.rmsr.myguard.domain.entity._History
import io.reactivex.rxjava3.core.Flowable

@Suppress("Future not implemented or tested yet.")
interface _HistoryRepository {
    fun observeHistory(): Flowable<List<_History>>
}