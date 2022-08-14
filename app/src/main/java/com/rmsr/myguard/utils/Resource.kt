package com.rmsr.myguard.utils

sealed class Resource<out T>(
    protected open val data: T? = null,
    protected open val message: String? = null,
    protected open val exception: Throwable? = null,
) {

    data class Success<T>(
        public override val data: T,
        public override val message: String? = null,
    ) : Resource<T>(data, message)

    data class Loading<T>(public override val data: T? = null) : Resource<T>(data)

    data class Error<T>(
        public override val exception: Throwable?,
        public override val message: String? = exception?.message,
        public override val data: T? = null,
    ) : Resource<T>(data, message, exception)
}