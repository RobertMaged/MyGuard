package com.rmsr.myguard.domain.usecase.schedules

import com.rmsr.myguard.domain.entity.SearchQuery
import com.rmsr.myguard.domain.entity.errors.AlreadyExistedRecordError
import com.rmsr.myguard.domain.entity.errors.ErrorEntity
import com.rmsr.myguard.domain.repository.ErrorHandler
import com.rmsr.myguard.domain.repository.ScheduleWriteRepo
import com.rmsr.myguard.domain.usecase.ValidateSearchQueryUseCase
import com.rmsr.myguard.domain.utils.Validator
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class AddScheduleUseCase @Inject constructor(
    private val scheduleRepo: ScheduleWriteRepo,
    private val queryValidation: ValidateSearchQueryUseCase,
    private val errorHandler: ErrorHandler,
) {

    fun add(search: SearchQuery): Single<Result<Unit>> {
        fun s(search: SearchQuery): Single<Result<Unit>> {
            return scheduleRepo.isSearchQueryScheduled(search)
                .flatMap { isExist ->
                    if (isExist)
                        Single.just(
                            Result.failure(AlreadyExistedRecordError("Schedule already existed."))
                        )
                    else
                        scheduleRepo.createNewSchedule(search)
                            .toSingleDefault(Result.success(Unit))
                }

                .onErrorResumeNext {
                    when (it) {
                        is ErrorEntity.ValidationError -> Single.just(Result.failure(it))
                        else -> Single.error(errorHandler.getError(it))
                    }
                }
        }

        return when (val result = queryValidation.invoke(search, autoCorrect = true)) {
            is Validator.Invalid -> Single.just(Result.failure(result.exception))
            is Validator.AutoCorrected -> s(result.correctedData)
            is Validator.Valid -> s(search)
        }
    }

    operator fun invoke(search: SearchQuery): Completable {
        fun s(search: SearchQuery): Completable {
            return scheduleRepo.isSearchQueryScheduled(search)
                .flatMapCompletable { isExist ->
                    if (isExist)
                        Completable.error(AlreadyExistedRecordError("Schedule already existed."))
                    else
                        scheduleRepo.createNewSchedule(search)
                }

                .onErrorResumeNext {
                    Completable.error(errorHandler.getError(it))
                }
        }
        return when (val result = queryValidation.invoke(search, autoCorrect = true)) {
            is Validator.Invalid -> Completable.error(result.exception)
            is Validator.AutoCorrected -> s(result.correctedData)
            is Validator.Valid -> s(search)
        }
    }
}