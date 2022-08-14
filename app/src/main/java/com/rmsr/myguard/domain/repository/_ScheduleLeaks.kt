package com.rmsr.myguard.domain.repository

import com.rmsr.myguard.domain.entity.Breach
import com.rmsr.myguard.domain.entity.ScheduleId
import io.reactivex.rxjava3.core.Maybe

@Deprecated("This data is specific to Domain layer needs and should computed and gathered there.")
interface _ScheduleLeaks {

    /**
     * Return breaches that user didn't notified about of a schedule.
     */
    @Deprecated("")
    fun getScheduleNewLeaks(scheduleId: ScheduleId): Maybe<List<Breach>>

    @Deprecated("")
    fun getSchedulesNewLeaks(schedulesIds: List<Long>): Maybe<Map<Long, List<Breach>>>

    /**
     * Return All breaches found of a schedule.
     */
    @Deprecated("")
    fun getScheduleAllLeaks(scheduleId: ScheduleId): Maybe<List<Breach>>
}

