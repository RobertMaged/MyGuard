package com.rmsr.myguard.presentation.util

import android.widget.Toast
import androidx.compose.material.SnackbarDuration

sealed class UserCommunicate(
    open val id: Long,
    open val msg: UiText,
) {

    data class ToastMessage(
        override val id: Long = System.currentTimeMillis(),
        override val msg: UiText,
        val duration: Int = Toast.LENGTH_SHORT,
    ) : UserCommunicate(id, msg)

    data class SnackbarMessage(
        override val id: Long = System.currentTimeMillis(),
        override val msg: UiText,
        val duration: SnackbarDuration = SnackbarDuration.Short,
        val actionLabel: String? = null,
        val onDismissed: () -> Unit = {},
        val onAction: () -> Unit = {},
    ) : UserCommunicate(id, msg)
}

fun UiText.inToast(
    id: Long = System.currentTimeMillis(),
    duration: Int = Toast.LENGTH_SHORT
): UserCommunicate.ToastMessage {
    return UserCommunicate.ToastMessage(id = id, msg = this, duration = duration)
}

fun UiText.inSnackbar(
    id: Long = System.currentTimeMillis(),
    duration: SnackbarDuration = SnackbarDuration.Short,
    actionLabel: String? = null,
    onDismissed: () -> Unit = {},
    onAction: () -> Unit = {}
): UserCommunicate.SnackbarMessage {
    return UserCommunicate.SnackbarMessage(
        id = id,
        msg = this,
        duration = duration,
        actionLabel = actionLabel,
        onDismissed = onDismissed,
        onAction = onAction
    )
}