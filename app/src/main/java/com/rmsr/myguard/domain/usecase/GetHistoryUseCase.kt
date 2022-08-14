package com.rmsr.myguard.domain.usecase

import com.rmsr.myguard.domain.entity._History
import com.rmsr.myguard.domain.repository._HistoryRepository
import com.rmsr.myguard.utils.MyLog
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

@Suppress("Future not implemented or tested yet.")
class GetHistoryUseCase @Inject constructor(
    private val history: _HistoryRepository
) {
    private val TAG = "Rob_GetHistoryUseCase"
    private val b = BehaviorSubject.createDefault(emptyList<_History>())
    private val f = b.doOnSubscribe { MyLog.d(TAG, "state subscribed") }
        .doOnNext { MyLog.d(TAG, "state new history size: ${it.size}") }
        .doOnDispose { MyLog.d(TAG, "state disposed") }
        .doOnComplete { MyLog.d(TAG, "state complete") }

    val lazy by lazy(LazyThreadSafetyMode.NONE) {
        history.observeHistory().toObservable()
            .doOnSubscribe { MyLog.d(TAG, "observer subscribed") }
            .doOnNext { MyLog.d(TAG, "observer new size: ${it.size}") }
            .doOnDispose { MyLog.d(TAG, "observer disposed") }
            .subscribe(b)
    }

    //init {
//    lazy
//}
    operator fun invoke(s: String): Maybe<List<String>> {
        lazy
        return Maybe.fromObservable(f)
            .map {
                it.filter { it.searchQuery.query.startsWith(s.trim()) }
            }
            .filter { it.isNotEmpty() }
            .map { it.map { it.searchQuery.query } }
            .doOnSuccess { MyLog.d(TAG, "here history sample: ${it.take(3)} - size= ${it.size}") }
            .doOnError { MyLog.d(TAG, "history error : $it") }
            .doOnComplete { MyLog.d(TAG, "no history found") }

    }
}