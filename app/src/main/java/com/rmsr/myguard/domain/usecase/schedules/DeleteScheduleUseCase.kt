package com.rmsr.myguard.domain.usecase.schedules

import com.rmsr.myguard.domain.entity.ScheduleId
import com.rmsr.myguard.domain.repository.ErrorHandler
import com.rmsr.myguard.domain.repository.ScheduleWriteRepo
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class DeleteScheduleUseCase @Inject constructor(
    private val scheduleRepo: ScheduleWriteRepo,
    private val errorHandler: ErrorHandler,
) {
    private val TAG = "Rob_DeleteScheduleUseCase"

    operator fun invoke(scheduleId: ScheduleId): Completable {

        return scheduleRepo.deleteSchedule(scheduleId = scheduleId)
            .onErrorResumeNext {
                Completable.error(errorHandler.getError(it))
            }
    }
}