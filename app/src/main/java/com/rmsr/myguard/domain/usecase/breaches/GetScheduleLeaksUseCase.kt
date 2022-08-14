package com.rmsr.myguard.domain.usecase.breaches

import com.rmsr.myguard.domain.entity.Breach
import com.rmsr.myguard.domain.entity.ScheduleId
import com.rmsr.myguard.domain.repository.BreachRepository
import com.rmsr.myguard.domain.repository.ScanRecordsRepository
import com.rmsr.myguard.utils.logTimeInterval
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.MaybeObserver
import javax.inject.Inject

class GetScheduleLeaksUseCase @Inject constructor(
    private val breachRepo: BreachRepository,
    private val recordsRepo: ScanRecordsRepository
) {
    private val TAG = "Rob_GetScheduleAllLeaks"

    /**
     * Return all [Breach]s this [scheduleId] appears in.
     *
     * Acknowledged breaches will also be included.
     *
     * @param scheduleId schedule id to retrieve its [Breach]s.
     *
     * @return [MaybeObserver.onSuccess] with found [Breach]s,
     *  or [MaybeObserver.onComplete] if no leaks found for this schedule.
     *
     *  @see GetScheduleNewLeaksUseCase.invoke
     */
    operator fun invoke(scheduleId: ScheduleId): Maybe<List<Breach>> {
        return recordsRepo.getScheduleRecords(scheduleId)
            .flatMap {
                val breachesIds = it.map { it.identifiers.breachId }.distinct()
                breachRepo.getBreachesByIds(breachesIds)
            }
            .logTimeInterval(TAG, "new ScheduleAllLeaK")

    }
}