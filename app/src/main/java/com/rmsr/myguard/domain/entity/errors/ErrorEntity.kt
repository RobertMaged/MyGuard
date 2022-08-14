package com.rmsr.myguard.domain.entity.errors

import com.rmsr.myguard.utils.MyLog

sealed class ErrorEntity(message: String, cause: Throwable?) :
    RuntimeException(message, cause) {


    sealed class ValidationError(message: String, cause: Throwable? = null) :
        ErrorEntity(message, cause) {
        init {
            MyLog.w("Rob_Validation", message, cause)
        }
    }

    open class ApiError(
        open val responseCode: Int,
        message: String,
        cause: Throwable?
    ) :
        ErrorEntity(message, cause) {

        class UnknownApiResponse(code: Int, msg: String, cause: Throwable?) :
            ApiError(code, msg, cause)
    }


    object NoNetwork : ErrorEntity("No Internet Connection", null) {
        init {
            MyLog.w("Rob_${this.javaClass.simpleName}", message, cause)
        }

    }

    class UnKnownError(message: String?, cause: Throwable?) :
        ErrorEntity(message ?: "UnKnownError", cause) {

        init {
            MyLog.w("Rob_UnKnownError", message, cause ?: Throwable())
        }
    }
}

class AlreadyExistedRecordError(
    message: String = "Already existed record.",
    cause: Throwable? = null,
) : ErrorEntity.ValidationError(message, cause)

//Todo implement this in data layer.
class NotExistedReferenceError(
    message: String = "This id has no existed reference.",
    cause: Throwable? = null,
) : ErrorEntity.ValidationError(message, cause)