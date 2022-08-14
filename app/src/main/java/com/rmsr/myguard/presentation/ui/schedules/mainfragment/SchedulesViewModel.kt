package com.rmsr.myguard.presentation.ui.schedules.mainfragment

import androidx.compose.material.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmsr.myguard.domain.entity.ScheduleId
import com.rmsr.myguard.domain.entity.ScheduleStuff
import com.rmsr.myguard.domain.entity.SessionId
import com.rmsr.myguard.domain.usecase.FormatDateTimeRelativelyUseCase
import com.rmsr.myguard.domain.usecase.schedules.DeleteScheduleUseCase
import com.rmsr.myguard.domain.usecase.schedules.ObserveSchedulesStuffUseCase
import com.rmsr.myguard.domain.usecase.session.MarkSessionRespondedUseCase
import com.rmsr.myguard.presentation.util.UserCommunicate
import com.rmsr.myguard.presentation.util.dynamicString
import com.rmsr.myguard.presentation.util.inSnackbar
import com.rmsr.myguard.presentation.util.inToast
import com.rmsr.myguard.utils.MyLog
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private data class SchedulesViewModelState(
    val schedules: List<ScheduleStuff> = emptyList(),
    val selectedScheduleId: ScheduleId = ScheduleId(-1),
    val tempHiddenSchedules: Set<ScheduleId> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessages: List<UserCommunicate> = emptyList(),
) {
    fun toUiState(): SchedulesUiState {
//        val totalLeaks = mutableSetOf<Breach>()

        val schedulesItems = schedules
            .filterNot { it.schedule.wrappedId in tempHiddenSchedules }

            .map { schedule: ScheduleStuff ->
//                totalLeaks += schedule.leaks.allLeaks

                val lastLeaksFormatted =
                    schedule.leaks.lastFoundLeaks.takeIf { it.isNotEmpty() }
                        ?.joinToString(limit = 4) { it.title } ?: "No Leaks"

                SchedulesItemUiState(
                    schedule = schedule.schedule,
                    newLeaksCount = schedule.leaks.notAcknowledged.count(),
                    lastLeaks = lastLeaksFormatted,
                    date = FormatDateTimeRelativelyUseCase.invoke(schedule.lastMassageTime),
                    isItemExpanded = schedule.schedule.wrappedId == selectedScheduleId
                )
            }

        return SchedulesUiState(
            isLoadingSchedules = isLoading,
            schedulesItems = schedulesItems,
            userMessages = errorMessages,
            totalFoundLeaksCount = 0,//totalLeaks.count(),
            showDefaultScreen = schedules.isEmpty()
        )
    }
}

@HiltViewModel
class SchedulesViewModel @Inject constructor(
    private val scheduleDelete: DeleteScheduleUseCase,
    private val schedulesObserve: ObserveSchedulesStuffUseCase,
    private val sessionUserRespond: MarkSessionRespondedUseCase,
) : ViewModel() {
    private val TAG = "Rob_SchedulesVM"

    private val disposables by lazy { CompositeDisposable() }

    private val viewModelState = MutableStateFlow(SchedulesViewModelState(isLoading = true))

    val uiState: StateFlow<SchedulesUiState> = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        observeSchedules()
    }

    fun deleteScheduleRetractable(scheduleId: ScheduleId) {
        val message = dynamicString("Schedule deleted").inSnackbar(
            duration = SnackbarDuration.Short, actionLabel = "Undo",
            onDismissed = {
                deleteSchedule(scheduleId)
            },
            //on Undo clicked
            onAction = {
                viewModelState.update {
                    it.copy(tempHiddenSchedules = it.tempHiddenSchedules - scheduleId)
                }
            })


        viewModelState.update {
            it.copy(
                tempHiddenSchedules = it.tempHiddenSchedules + scheduleId,
                errorMessages = it.errorMessages + message
            )
        }


    }

    fun expandSchedule(scheduleId: ScheduleId) {
        viewModelState.update {
            val schToExpand =
                if (it.selectedScheduleId != scheduleId) scheduleId else ScheduleId(-1)
            it.copy(selectedScheduleId = schToExpand)
        }
    }

    fun deleteSchedule(scheduleId: ScheduleId) {
        scheduleDelete(scheduleId = scheduleId)
            .subscribe(
                {
                    viewModelState.update {
                        it.copy(tempHiddenSchedules = it.tempHiddenSchedules - scheduleId)
                    }
                },
                // on error
                { MyLog.e(TAG, "Delete Error $it", logThreadName = true) }
            )
    }

    /**
     * When user navigate to this fragment from notification.
     *
     * @param sessionId which sent from navigation args or intent.
     */
    fun markSessionUserResponded(sessionId: Long) {
        sessionUserRespond.invoke(sessionId = SessionId(sessionId))
            .doOnError { MyLog.e(TAG, "user respond Error ${it.message}") }
            .subscribe({}, {})
    }


    fun observeSchedules() {
        schedulesObserve.invoke()
            .subscribe(
                //onNext
                { result ->

                    viewModelState.update {
                        it.copy(
                            schedules = result,
                            isLoading = false
                        )
                    }
                },

                { e ->
                    MyLog.e(TAG, "allsaved mails onError: ${e.message}")

                    val message = dynamicString(e.message.orEmpty()).inToast()

                    viewModelState.update {
                        it.copy(errorMessages = it.errorMessages + message, isLoading = false)
                    }
                },

                // onComplete - stream
                {
                    viewModelState.update { it.copy(isLoading = false) }
                }
            )
            .also { disposable -> disposables.add(disposable) }
    }


    fun userMessageShown(msgId: Long) {
        viewModelState.update {
            val messages = it.errorMessages.filterNot { it.id == msgId }
            it.copy(errorMessages = messages)
        }
    }


    private fun UserCommunicate.showMsg() = viewModelState.update { prev ->
        prev.copy(errorMessages = prev.errorMessages + this)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
