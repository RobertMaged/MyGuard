package com.rmsr.myguard.domain.usecase.breaches

import com.rmsr.myguard.domain.entity.Breach
import com.rmsr.myguard.domain.entity.ScanRecord
import com.rmsr.myguard.domain.entity.ScheduleId
import com.rmsr.myguard.domain.repository.BreachRepository
import com.rmsr.myguard.domain.repository.ScanRecordsRepository
import com.rmsr.myguard.utils.logTimeInterval
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.MaybeObserver
import java.util.*
import javax.inject.Inject

class GetScheduleNewLeaksUseCase @Inject constructor(
    private val breach: BreachRepository,
    private val record: ScanRecordsRepository
) {
    private val TAG = "Rob_ScheduleUnAckLeaks"


    /**
     * Return [Breach]s this [scheduleId] appears in and user didn't acknowledged about them before.
     *
     * @param scheduleId schedule id to retrieve its [Breach]s.
     *
     * @return [MaybeObserver.onSuccess] with found new [Breach]s,
     *  or [MaybeObserver.onComplete] if no leaks found for this schedule or user already saw them before.
     *
     *  @see GetScheduleLeaksUseCase.invoke
     */
    operator fun invoke(scheduleId: ScheduleId): Maybe<List<Breach>> {
        return record.getScheduleRecords(scheduleId)

            .mapOptional { records: List<ScanRecord> ->
                return@mapOptional records
                    .filterNot { it.recordInfo.userAcknowledged }

                    .map { it.identifiers.breachId }

                    .takeIf { it.isNotEmpty() }

                    ?.let { Optional.of(it) } ?: Optional.empty()
            }

            .flatMap {
                breach.getBreachesByIds(it)
            }
            .logTimeInterval(TAG, "New ScheduleNewLeaK")
    }
}

