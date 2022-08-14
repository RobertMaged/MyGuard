package com.rmsr.myguard.presentation.util

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.widget.Toast

/**
 * Convert source text which contains any html style code to actual
 * new styled text can be displayed in text view.
 *
 * This method can handle different build SDKs.
 * @see Html.fromHtml
 */
fun getHTMLFormatText(source: String): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(source, Html.FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE)
    } else {
        Html.fromHtml(source)
    }
}

fun Context.displayToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

val <T> T.exhaustive: T
    get() = this