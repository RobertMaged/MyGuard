package com.rmsr.myguard.domain.usecase.schedules

import com.rmsr.myguard.domain.entity.Schedule
import com.rmsr.myguard.domain.entity.ScheduleId
import com.rmsr.myguard.domain.repository.ScheduleRetrieveRepo
import io.reactivex.rxjava3.core.Maybe
import javax.inject.Inject

class GetScheduleUseCase @Inject constructor(
    private val scheduleRetrieve: ScheduleRetrieveRepo,
) {
    operator fun invoke(scheduleId: Long): Maybe<Schedule> =
        scheduleRetrieve.getSchedule(scheduleId = ScheduleId(scheduleId))

    operator fun invoke(scheduleId: ScheduleId): Maybe<Schedule> =
        scheduleRetrieve.getSchedule(scheduleId = scheduleId)


}