package com.rmsr.myguard.presentation.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiText {
    @Composable
    abstract fun asString(): String

    abstract fun asString(context: Context): String

    data class DynamicString(val msg: String) : UiText() {
        @Composable
        override fun asString(): String = msg

        override fun asString(context: Context): String = msg
    }


    class StringResource(@StringRes val resId: Int, vararg val formatArgs: Any) : UiText() {
        @Composable
        override fun asString(): String = stringResource(resId, *formatArgs)

        override fun asString(context: Context): String = context.getString(resId, *formatArgs)
    }

}

fun dynamicString(msg: String): UiText.DynamicString = UiText.DynamicString(msg)

fun resourceString(@StringRes resId: Int, vararg formatArgs: Any): UiText =
    UiText.StringResource(resId, formatArgs)