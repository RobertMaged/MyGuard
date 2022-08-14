package com.rmsr.myguard.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.rmsr.myguard.data.remote.LeaksApiError
import com.rmsr.myguard.domain.entity.errors.AlreadyExistedRecordError
import com.rmsr.myguard.domain.entity.errors.ErrorEntity
import com.rmsr.myguard.domain.repository.ErrorHandler
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ErrorHandleImpl @Inject constructor() : ErrorHandler {
    override fun getError(throwable: Throwable): ErrorEntity = when (throwable) {
        is ErrorEntity -> throwable
        is SQLiteConstraintException -> AlreadyExistedRecordError(
            throwable.message.orEmpty(),
            cause = throwable
        )
        is LeaksApiError -> ErrorEntity.ApiError(
            throwable.responseCode,
            throwable.message.orEmpty(),
            throwable.cause
        )
        is HttpException -> delegateHandlerByHost(throwable)
        is IOException -> ErrorEntity.NoNetwork

        else -> ErrorEntity.UnKnownError(
            throwable.message ?: "Unknown Error.",
            throwable
        )
    }

    private fun delegateHandlerByHost(
        httpException: HttpException,
        host: String? = httpException.response()?.raw()?.request()?.url()?.host(),
    ): ErrorEntity.ApiError {
        return when {

            host?.contains(
                "haveibeenpwned.com",
                ignoreCase = true
            ) == true -> LeaksApiError.handleResponse(httpException)
                .let {
                    ErrorEntity.ApiError(
                        responseCode = it.responseCode,
                        message = it.message.orEmpty(),
                        cause = it.cause
                    )
                }
            //                .contains("api.pwnedpasswords.com", ignoreCase = true)

            else -> ErrorEntity.ApiError(
                responseCode = httpException.code(),
                message = httpException.message(), cause = httpException
            )
        }
    }


}