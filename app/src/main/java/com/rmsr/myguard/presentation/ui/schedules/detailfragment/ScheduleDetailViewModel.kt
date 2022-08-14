package com.rmsr.myguard.presentation.ui.schedules.detailfragment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.rmsr.myguard.domain.entity.Breach
import com.rmsr.myguard.domain.entity.ScheduleId
import com.rmsr.myguard.domain.usecase.breaches.GetScheduleLeaksUseCase
import com.rmsr.myguard.domain.usecase.breaches.GetScheduleNewLeaksUseCase
import com.rmsr.myguard.domain.usecase.breaches.SortBreachesUseCase
import com.rmsr.myguard.domain.usecase.schedules.MarkScheduleRecordsSeenUseCase
import com.rmsr.myguard.utils.MyLog
import com.rmsr.myguard.utils.RxSchedulers
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.MaybeObserver
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ScheduleDetailViewModel @Inject constructor(
    private val getLatestLeaks: GetScheduleNewLeaksUseCase,
    private val getAllLeaks: GetScheduleLeaksUseCase,
    private val markSchedules: MarkScheduleRecordsSeenUseCase,
    private val sortBreaches: SortBreachesUseCase,
    private val rxSchedulers: RxSchedulers
) : ViewModel() {

    var leaks by mutableStateOf<List<Breach>>(emptyList())
        private set

    private val disposables by lazy { CompositeDisposable() }
    private val observer = object : MaybeObserver<List<Breach>> {
        override fun onSubscribe(d: Disposable) {
            disposables.add(d)
        }

        override fun onSuccess(t: List<Breach>) {
            leaks = sortBreaches.byDateDescending(t)
        }

        override fun onError(e: Throwable) = e.printStackTrace()

        override fun onComplete() {
            leaks = emptyList()
        }
    }

    fun loadScheduleAllLeaks(scheduleId: Long) =
        getAllLeaks.invoke(ScheduleId(scheduleId))
            .doAfterSuccess { markScheduleLeaksAsSeen(scheduleId) }
            .subscribe(observer)


    fun loadScheduleNewLeaks(scheduleId: Long) =
        getLatestLeaks.invoke(ScheduleId(scheduleId))
            .doAfterSuccess { markScheduleLeaksAsSeen(scheduleId) }
            .subscribe(observer)

    private fun markScheduleLeaksAsSeen(scheduleId: Long) {
        markSchedules.invoke(ScheduleId(scheduleId))
            .delaySubscription(2_000L, TimeUnit.MILLISECONDS, rxSchedulers.io())
            .doOnComplete { MyLog.d(TAG, "schedule marked seen.") }
            .subscribe()
            .also { disposable ->
                disposables.add(disposable)
            }
    }

    override fun onCleared() {
        disposables.clear()
    }

    companion object {
        private const val TAG = "Rob_ScheduleDetailViewModel"
    }
}