package com.rmsr.myguard.data.remote

import com.rmsr.myguard.utils.MyLog
import retrofit2.HttpException

/*
    200	Ok — everything worked and there's a string array of pwned sites for the account
    */
sealed class LeaksApiError(val responseCode: Int, message: String, cause: Throwable?) :
    RuntimeException(message, cause) {


    init {
        //because msg is not final, null value must be checked.
        MyLog.w("Rob_LeakApiError", message, cause)
    }

    //    400	Bad request — the account does not comply with an acceptable format (i.e. it's an empty string)
    class BadRequest(
        code: Int = 400,
        msg: String = "the account does not comply with an acceptable format.",
        cause: Throwable?,
    ) : LeaksApiError(code, msg, cause)

    //    401	Unauthorised — either no API key was provided or it wasn't valid
    class Unauthorised(
        code: Int = 401,
        msg: String = "either no API key was provided or it wasn't valid.",
        cause: Throwable?,
    ) : LeaksApiError(code, msg, cause)

    //    403	Forbidden — no user agent has been specified in the request
    class Forbidden(
        code: Int = 403,
        msg: String = "no user agent has been specified in the request.",
        cause: Throwable?,
    ) : LeaksApiError(code, msg, cause)

    class NoLeaksFound(
        code: Int = 404,
        msg: String = "the account could not be found and has therefore not been pwned",
        cause: Throwable?,
    ) : LeaksApiError(code, msg, cause)

    //    429	Too many requests — the rate limit has been exceeded
    data class TooManyRequests(
        val waitMilliSeconds: Int,
        override val message: String = "Rate limit is exceeded. Try again in ${waitMilliSeconds / 1000} seconds.",

        override val cause: Throwable?,
    ) : LeaksApiError(429, message, cause)

    //    503	Service unavailable — usually returned by Cloudflare if the underlying service is not available
    class ServiceUnavailable(code: Int = 503, msg: String, cause: Throwable?) :
        LeaksApiError(code, msg, cause)

    class UnExpectedResponse(code: Int, msg: String, cause: Throwable?) :
        LeaksApiError(code, msg, cause)


    companion object {
        fun handleResponse(throwable: HttpException): LeaksApiError =
            when (throwable.code()) {
                200 -> throw NotImplementedError(throwable.message ?: "Should not pass here!!")
                400 -> BadRequest(cause = throwable)
                401 -> Unauthorised(
                    msg = throwable.message().takeIf { it.isNotBlank() }
                        ?: "either no API key was provided or it wasn't valid.",
                    cause = throwable)
                403 -> Forbidden(cause = throwable)
                404 -> NoLeaksFound(cause = throwable)
                503 -> ServiceUnavailable(
                    msg = throwable.message().orEmpty(),
                    cause = throwable
                )

                429 -> {
                    val retryAfter =
                        throwable.response()?.headers()?.get("retry-after")?.toIntOrNull() ?: 2

                    TooManyRequests(cause = throwable, waitMilliSeconds = retryAfter * 1000)
                }

                else -> UnExpectedResponse(
                    throwable.code(),
                    throwable.message().orEmpty(), cause = throwable
                )
            }
    }
}
