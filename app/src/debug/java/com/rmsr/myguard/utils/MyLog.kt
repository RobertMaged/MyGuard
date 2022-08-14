package com.rmsr.myguard.utils

import android.util.Log
import com.rmsr.myguard.BuildConfig

object MyLog {
    private val thread: String
        get() = Thread.currentThread().name

    private val isDebug: Boolean
        get() = BuildConfig.DEBUG

    @JvmStatic
    @JvmOverloads
    fun d(tag: String?, msg: String, logThreadName: Boolean = false) {
        if (logThreadName)
            Log.d("${thread}: $tag", msg)
        else
            Log.d(tag, msg)
    }

    @JvmStatic
    @JvmOverloads
    fun d(tag: String, msg: String?, tr: Throwable?, logThreadName: Boolean = false) {
        if (logThreadName)
            Log.d("$thread: $tag", msg, tr)
        else
            Log.d(tag, msg, tr)
    }

    @JvmStatic
    @JvmOverloads
    fun e(tag: String?, msg: String, logThreadName: Boolean = false) {
        if (logThreadName)
            Log.e("${thread}: $tag", msg)
        else
            Log.e(tag, msg)
    }

    @JvmStatic
    @JvmOverloads
    fun e(
        tag: String?,
        msg: String?,
        tr: Throwable?,
        logThreadName: Boolean = false,
        throwIfDebugApp: Boolean = false,
    ) {
        if (logThreadName)
            Log.e("${thread}: $tag", msg, tr)
        else
            Log.e(tag, msg, tr)


        if (throwIfDebugApp && isDebug && tr != null)
            throw tr
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
        when {
            logThreadName -> Log.w("${thread}: $tag", msg, tr)
            else -> Log.w(tag, msg, tr)
        }

        if (throwIfDebugApp && isDebug && tr != null)
            throw tr
    }
}
