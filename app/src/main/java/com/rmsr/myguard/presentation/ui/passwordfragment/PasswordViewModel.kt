package com.rmsr.myguard.presentation.ui.passwordfragment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmsr.myguard.domain.usecase.breaches.CountPasswordLeaksUseCase
import com.rmsr.myguard.presentation.util.UserCommunicate
import com.rmsr.myguard.presentation.util.dynamicString
import com.rmsr.myguard.presentation.util.inToast
import com.rmsr.myguard.utils.MyLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject


private data class PasswordViewModelState(
    val isLoading: Boolean = false,
    val countIfLeaked: Int = 0,
    val passwordQuery: String = "",
    val resultReady: Boolean = false,
    val errorMessages: List<UserCommunicate> = emptyList(),
) {

    fun toUiState(): PasswordUiState {
        return PasswordUiState(
            isLoading = isLoading,
            passwordQuery = passwordQuery, passwordLeaksCount = countIfLeaked,
            isResultReady = resultReady,
            errorMessages = errorMessages
        )
    }
}

@HiltViewModel
class PasswordViewModel @Inject constructor(
    private val searchPassword: CountPasswordLeaksUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val TAG = "Rob_PasswordViewModel"
    private val QUERY_STATE = "query"

    private val viewModelState = MutableStateFlow(
        PasswordViewModelState(
            isLoading = false,
            passwordQuery = savedStateHandle.get<String>(QUERY_STATE).orEmpty()
        )
    )

    init {
        savedStateHandle.getStateFlow(QUERY_STATE, "")


            .onEach {
                viewModelState.emit(
                    viewModelState.value.copy(passwordQuery = it, resultReady = false)
                )
            }

            .catch { }
            .launchIn(viewModelScope)
    }

    var passwordQuery: String = viewModelState.value.passwordQuery
        set(value) {
            field = value
            savedStateHandle[QUERY_STATE] = value
        }

    val uiState: StateFlow<PasswordUiState>
        get() = viewModelState
            .map { it.toUiState() }
            .distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.Lazily, viewModelState.value.toUiState())


    fun userMessageShown(msgId: Long) {
        viewModelState.update {
            val messages = it.errorMessages.filterNot { it.id == msgId }
            it.copy(errorMessages = messages)
        }
    }

    fun searchForPassword() {

        viewModelState.update {
            if (it.passwordQuery.isNotBlank())
                it.copy(isLoading = true, resultReady = false)
            else
                it.copy(
                    resultReady = false,
                    errorMessages =
                    it.errorMessages + dynamicString("Password can not be blank").inToast()
                )
        }

        val password = viewModelState.value.passwordQuery.takeIf { it.isNotBlank() } ?: return

        searchPassword.invoke(password)
            .subscribe(
                //on Leaks found
                { count ->
                    viewModelState.update {
                        it.copy(
                            countIfLeaked = count,
                            isLoading = false, resultReady = true
                        )
                    }
                },

                { throwable ->
                    val error = dynamicString(throwable.message.orEmpty()).inToast()

                    viewModelState.update {
                        it.copy(
                            isLoading = false, resultReady = false,
                            errorMessages = it.errorMessages + error
                        )
                    }

                    MyLog.e(TAG, "Password Error: ${throwable.message}")
                },

                //on no leaks return null
                {
                    viewModelState.update {
                        it.copy(
                            countIfLeaked = 0,
                            resultReady = true,
                            isLoading = false
                        )
                    }
                }
            )
    }


}
