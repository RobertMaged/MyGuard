package com.rmsr.myguard.domain.usecase.schedules

import com.rmsr.myguard.domain.entity.ScheduleId
import com.rmsr.myguard.domain.repository.ScanRecordsRepository
import com.rmsr.myguard.utils.component1
import com.rmsr.myguard.utils.component2
import com.rmsr.myguard.utils.onEmptyListComplete
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class MarkScheduleRecordsSeenUseCase @Inject constructor(
    private val scheduleLeaks: ScanRecordsRepository,
) {
    /**
     * Mark all existed records this [scheduleId] appears in as seen which means that user acknowledged about them.
     *
     * So when consumer ask about schedule new records - acknowledged items will not appear again.
     *
     * This use case is not aware if this schedule records already retrieved before or not.
     *
     * @param scheduleId that all its records will updated.
     */
    operator fun invoke(scheduleId: ScheduleId): Completable {
        return scheduleLeaks.getScheduleRecords(scheduleId)
            .map { it.filterNot { it.recordInfo.userAcknowledged } }
            .onEmptyListComplete()

            .timestamp()
            .map { (time, scanRecords) ->
                scanRecords.map { record ->
                    val recordInfo =
                        record.recordInfo.copy(userAcknowledged = true, acknowledgeTime = time)
                    record.copy(recordInfo = recordInfo)
                }
            }

            .map { it.toSet() }

            .flatMapCompletable(scheduleLeaks::updateScanRecords)
    }

    @Deprecated(
        message = "Pass id with ScheduleId instead of Long, this method may be deleted later.",
        replaceWith = ReplaceWith(
            "invoke(ScheduleId(value = ))",
            "com.rmsr.myguard.domain.model.ScheduleId"
        )
    )
    operator fun invoke(scheduleId: Long): Completable {
        return scheduleLeaks.scheduleRecordsAcknowledged(scheduleId = ScheduleId(scheduleId))
    }
}