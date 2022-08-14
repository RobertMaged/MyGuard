package com.rmsr.myguard.domain.usecase.breaches

import com.rmsr.myguard.domain.entity.errors.ErrorEntity
import com.rmsr.myguard.domain.entity.errors.InvalidPasswordException
import com.rmsr.myguard.domain.repository.ErrorHandler
import com.rmsr.myguard.domain.repository.PasswordRepository
import com.rmsr.myguard.domain.usecase.EncryptPasswordToSHA1UseCase
import io.reactivex.rxjava3.core.Maybe
import javax.inject.Inject


class CountPasswordLeaksUseCase @Inject constructor(
    private val repository: PasswordRepository,
    private val errorHandler: ErrorHandler,

    ) {
    private val repositoryName: String = repository.javaClass.simpleName

    operator fun invoke(password: String): Maybe<Int> {
        password.ifBlank { return Maybe.error(InvalidPasswordException()) }

        val hashedPassword = EncryptPasswordToSHA1UseCase.invoke(password)
        return repository.findPasswordLeaksCount(hashedPassword)
            .onErrorResumeNext { Maybe.error(errorHandler.getError(it)) }
    }

    fun search(password: String): Maybe<Result<Int>> {
        password.ifBlank { return Maybe.just(Result.failure(InvalidPasswordException())) }

        val hashedPassword = EncryptPasswordToSHA1UseCase.invoke(password)
        return repository.findPasswordLeaksCount(hashedPassword)
            .map { Result.success(it) }
            .onErrorResumeNext {
                when (it) {
                    is ErrorEntity.ValidationError -> Maybe.just(Result.failure(it))
                    else -> Maybe.error(errorHandler.getError(it))
                }
            }
    }


    companion object {
        private const val TAG = "Rob_SearchPasswordUseCase"
    }
}