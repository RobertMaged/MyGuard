package com.rmsr.myguard.domain.repository

import com.rmsr.myguard.domain.entity.Schedule
import com.rmsr.myguard.domain.entity.ScheduleId
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableSubscriber
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.MaybeObserver

interface ScheduleRetrieveRepo {
    /**
     * Return all [Schedule]s existed.
     *
     * @return [MaybeObserver.onSuccess] with all schedules,
     *      or [MaybeObserver.onComplete] if no schedules created.
     */
    fun getAllSchedules(): Maybe<List<Schedule>>

    /**
     *
     * @return [MaybeObserver.onSuccess] with specified schedule id,
     *      or [MaybeObserver.onComplete] if no schedule found with this id.
     */
    fun getSchedule(scheduleId: ScheduleId): Maybe<Schedule>

    /**
     *
     * @return [MaybeObserver.onSuccess] with specified schedules ids,
     *      or [MaybeObserver.onComplete] if no schedules found with this ids.
     */
    fun getSchedules(schedulesIds: Set<ScheduleId>): Maybe<List<Schedule>>

    /**
     * Return all [Schedule]s and Keep updated with changes happen to them.
     *
     * This flowable never calls [FlowableSubscriber.onComplete].
     *
     * @return All [Schedule]s existed, or empty list if no schedules created.
     */
    fun observeSchedules(): Flowable<List<Schedule>>
}