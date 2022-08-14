package com.rmsr.myguard.domain.usecase.schedules

import com.rmsr.myguard.domain.entity.ScheduleId
import com.rmsr.myguard.domain.entity.SearchQuery
import com.rmsr.myguard.domain.repository.ScheduleRetrieveRepo
import com.rmsr.myguard.domain.repository.ScheduleWriteRepo
import com.rmsr.myguard.domain.usecase.ValidateSearchQueryUseCase
import com.rmsr.myguard.domain.utils.Validator
import com.rmsr.myguard.utils.MyLog
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject


class EditScheduleQueryUseCase @Inject constructor(
    private val schedulesWrite: ScheduleWriteRepo,
    private val schedulesRetrieve: ScheduleRetrieveRepo,
    private val queryValidation: ValidateSearchQueryUseCase,
) {
    private val TAG = "Rob_EditScheduleUseCase"
    private val repositoryName: String = schedulesWrite.javaClass.simpleName


    operator fun invoke(scheduleId: ScheduleId, newQuery: SearchQuery): Completable {
        return when (
            val result = queryValidation.invoke(searchQuery = newQuery, autoCorrect = true)
        ) {

            is Validator.Invalid -> Completable.error(result.exception)

            is Validator.AutoCorrected -> updateScheduleQuery(scheduleId, result.correctedData)

            Validator.Valid -> updateScheduleQuery(scheduleId, newQuery)

        }
            .doOnComplete {
                MyLog.d(TAG, "$repositoryName: Editing Schedule Complete.", logThreadName = true)
            }
    }


    private fun updateScheduleQuery(scheduleId: ScheduleId, query: SearchQuery): Completable {

        return schedulesRetrieve.getSchedule(scheduleId)

            .map { it.copy(searchQuery = query) }

            .flatMapCompletable(schedulesWrite::editSchedule)
    }

}