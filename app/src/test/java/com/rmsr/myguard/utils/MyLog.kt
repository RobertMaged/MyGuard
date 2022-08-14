package com.rmsr.myguard.utils


object MyLog {
    @JvmStatic
    @JvmOverloads
    fun d(tag: String?, msg: String, logThreadName: Boolean = false) = Unit

    @JvmStatic
    @JvmOverloads
    fun d(tag: String, msg: String?, tr: Throwable?, logThreadName: Boolean = false) = Unit

    @JvmStatic
    @JvmOverloads
    fun e(tag: String?, msg: String, logThreadName: Boolean = false) = Unit

    @JvmStatic
    @JvmOverloads
    fun e(
        tag: String?,
        msg: String?,
        tr: Throwable?,
        logThreadName: Boolean = false,
        throwIfDebugApp: Boolean = false
    ) = Unit

    @JvmStatic
    @JvmOverloads
    fun w(
        tag: String?,
        msg: String?,
        tr: Throwable?,
        logThreadName: Boolean = false,
        throwIfDebugApp: Boolean = false
    ) = Unit
}
