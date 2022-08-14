package com.rmsr.myguard.presentation.ui.passwordfragment

import com.rmsr.myguard.presentation.util.UserCommunicate

data class PasswordUiState(
    val isLoading: Boolean = false,
    val passwordQuery: String = "",
    val passwordLeaksCount: Int = 0,
    val isResultReady: Boolean = false,
    val errorMessages: List<UserCommunicate> = emptyList(),
) {
    val isPasswordLeaked
        get() = passwordLeaksCount > 0
}