package com.rmsr.myguard.domain.repository

import com.rmsr.myguard.domain.entity.Schedule
import com.rmsr.myguard.domain.entity.ScheduleId
import com.rmsr.myguard.domain.entity.SearchQuery
import com.rmsr.myguard.domain.entity.errors.AlreadyExistedRecordError
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface ScheduleWriteRepo {
    /**
     * Create [Schedule] with this [query] so it can checked for leaks periodically.
     *
     * @throws AlreadyExistedRecordError if this [query] is already scheduled.
     *
     * @see isSearchQueryScheduled
     */
    fun createNewSchedule(query: SearchQuery): Completable

    fun editSchedule(schedule: Schedule): Completable

    fun deleteSchedule(scheduleId: ScheduleId): Completable

    /**
     * Check for this [query] if there is a [Schedule] created for it or not.
     *
     * @param query [SearchQuery] to search for.
     *
     * @return true if this [SearchQuery] is already scheduled.
     *          false if no [Schedule] created with this [query].
     */
    fun isSearchQueryScheduled(query: SearchQuery): Single<Boolean>
}

