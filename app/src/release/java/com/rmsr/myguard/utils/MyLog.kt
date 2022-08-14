package com.rmsr.myguard.utils

import android.util.Log

object MyLog {


    @JvmStatic
    @JvmOverloads
    fun d(tag: String?, msg: String, logThreadName: Boolean = false) = Unit

    @JvmStatic
    @JvmOverloads
    fun d(tag: String, msg: String?, tr: Throwable?, logThreadName: Boolean = false) = Unit

    @JvmStatic
    @JvmOverloads
    fun e(tag: String?, msg: String, logThreadName: Boolean = false) {
        Log.e(tag, msg)
    }

    @JvmStatic
    @JvmOverloads
    fun e(
        tag: String?,
        msg: String?,
        tr: Throwable?,
        logThreadName: Boolean = false,
    ) {
        Log.e(tag, msg, tr)
    }

    @JvmStatic
    @JvmOverloads
    fun w(
        tag: String?,
        msg: String?,
        tr: Throwable?,
        logThreadName: Boolean = false,
        throwIfDebugApp: Boolean = false,
    ) {
        Log.w(tag, msg, tr)
    }
}
