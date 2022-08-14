package com.rmsr.myguard.presentation.ui.schedules.addschedulefragment

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentResolverCompat
import androidx.core.database.getStringOrNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmsr.myguard.domain.entity.QueryType
import com.rmsr.myguard.domain.entity.Schedule
import com.rmsr.myguard.domain.entity.ScheduleId
import com.rmsr.myguard.domain.entity.SearchQuery
import com.rmsr.myguard.domain.entity.errors.AlreadyExistedRecordError
import com.rmsr.myguard.domain.entity.errors.InvalidSearchQueryException
import com.rmsr.myguard.domain.usecase.ValidateSearchQueryUseCase
import com.rmsr.myguard.domain.usecase.schedules.AddScheduleUseCase
import com.rmsr.myguard.domain.usecase.schedules.EditScheduleQueryUseCase
import com.rmsr.myguard.domain.usecase.schedules.GetScheduleUseCase
import com.rmsr.myguard.domain.utils.Validator
import com.rmsr.myguard.presentation.util.UserCommunicate
import com.rmsr.myguard.presentation.util.dynamicString
import com.rmsr.myguard.presentation.util.inSnackbar
import com.rmsr.myguard.presentation.util.inToast
import com.rmsr.myguard.utils.CoroutinesDispatchers
import com.rmsr.myguard.utils.MyLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private data class AddScheduleViewModelState
    (
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val query: String = "",
    val hint: String? = null,
    val queryType: QueryType = QueryType.EMAIL,
    val canPickerHandled: Boolean = true,
    val openContactsPicker: Boolean = false,
    val fragmentSafeToClose: Boolean = false,
    val errorMessages: List<UserCommunicate> = emptyList(),
) {
    fun toUiState(validator: ValidateSearchQueryUseCase): AddScheduleUiState {
        val result = validator.invoke(
            SearchQuery.from(query.trim(), queryType),
            autoCorrect = queryType == QueryType.Phone
        )

        val isValidQuery = when (result) {
            is Validator.Valid, is Validator.AutoCorrected -> true
            else -> false
        }


        return AddScheduleUiState(
            isValidQuery = isValidQuery,
            searchQuery = SearchQuery.from(query, queryType, hint),
            canPickerHandled = canPickerHandled,
            contactPickerRequired = openContactsPicker,
            scheduleSaved = fragmentSafeToClose,
            errorMessages = errorMessages
        )
    }

}

@HiltViewModel
class AddScheduleViewModel @Inject constructor(
    private val scheduleEdit: dagger.Lazy<EditScheduleQueryUseCase>,
    private val addSchedule: AddScheduleUseCase,
    private val getSchedule: dagger.Lazy<GetScheduleUseCase>,
    private val validateQuery: ValidateSearchQueryUseCase,
    private val dispatchers: CoroutinesDispatchers
) : ViewModel() {
    @Deprecated("use viewModelState instead", ReplaceWith("viewModelState"))
    private val _schedule2: MutableLiveData<Schedule> = MutableLiveData()

    @Deprecated("use uiState instead", ReplaceWith("uiState"))
    val schedule2: LiveData<Schedule>
        get() = _schedule2


    private val viewModelState = MutableStateFlow(AddScheduleViewModelState())

    val uiState: StateFlow<AddScheduleUiState> = viewModelState
        .map { it.toUiState(validateQuery) }
        .distinctUntilChanged()
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            viewModelState.value.toUiState(validateQuery)
        )

    fun sendEvent(event: AddScheduleUiEvents) = when (event) {
        is AddScheduleUiEvents.SetHint -> setHint(event.hint)
        is AddScheduleUiEvents.SetQuery -> setQuery(event.query)
        is AddScheduleUiEvents.SetCategory -> setCategory(event.type)

        is AddScheduleUiEvents.EndIconClick -> endIconClicked()
        is AddScheduleUiEvents.PickContact -> pickerFinished(event.resolver, event.uri)

        is AddScheduleUiEvents.SaveSchedule -> saveSchedule()
    }

    fun userMessageDisplayed(msgId: Long) {
        viewModelState.update { prev ->
            val messages = prev.errorMessages.filterNot { it.id == msgId }
            prev.copy(errorMessages = messages)
        }
    }

    fun setContactsPickerNotAvailable() =
        viewModelState.update { it.copy(canPickerHandled = false) }

    fun setEditModeState(inEditMode: Boolean, lazyScheduleId: () -> Long) {
        if (inEditMode.not())
            return

        TODO("Edit mode note implemented yet")

        viewModelState.update { it.copy(isLoading = true, isEditMode = true) }
        getSchedule(lazyScheduleId())
    }


    private fun setQuery(query: String) = viewModelState.update {
        it.copy(query = query)
    }

    private fun setHint(hint: String) {
        val optionalHint = hint.takeIf { it.isNotBlank() }
        viewModelState.update {
            it.copy(hint = optionalHint)
        }
    }

    private fun setCategory(queryType: QueryType) = viewModelState.update {
        it.copy(queryType = queryType)
    }

    private fun endIconClicked() = viewModelState.update {
        when {
            it.canPickerHandled && it.query.isEmpty() && it.queryType == QueryType.Phone ->
                return@update it.copy(openContactsPicker = true)

            it.query.isNotEmpty() -> return@update it.copy(query = "")

            else -> return
        }
    }

    private fun saveSchedule() {
        val state = viewModelState.value
        var searchQuery = SearchQuery.from(
            query = state.query.trim(),
            queryType = state.queryType,
            hint = state.hint?.trim()
        )

        val validationResult =
            validateQuery(searchQuery, autoCorrect = state.queryType == QueryType.Phone)

        when (validationResult) {
            is Validator.Invalid -> {
                errorHandler(validationResult.exception)
                return
            }
            is Validator.AutoCorrected -> searchQuery = validationResult.correctedData
            Validator.Valid -> Unit
        }

        addSchedule(search = searchQuery)
            .subscribe(
                // onComplete
                { viewModelState.update { it.copy(fragmentSafeToClose = true) } },

                // onError
                { e ->
                    val error = when (e) {
                        is AlreadyExistedRecordError -> dynamicString(
                            e.message ?: "Already existed"
                        ).inSnackbar()
                        else -> dynamicString(e.message ?: "unknown error").inToast()
                    }

                    error.showMsg()
                    Log.e(TAG, "onEmailInsertionError: ${e.message}")
                }
            )
    }

    private fun pickerFinished(resolver: ContentResolver, uri: Uri?) {

        viewModelState.update { it.copy(openContactsPicker = false) }

        if (uri == null) {
            dynamicString("No contact selected.").inSnackbar().showMsg()
            return
        }

        loadContactData(resolver, uri)
    }

    private fun loadContactData(resolver: ContentResolver, uri: Uri) =
        viewModelScope.launch(dispatchers.io()) {
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY
            )

            val cursor = ContentResolverCompat.query(
                resolver, uri, projection,
                null, null, null, null
            )

            if (cursor == null) {
                dynamicString("Can't import contact.").inSnackbar().showMsg()
                return@launch
            }

            getContactFromCursor(cursor)
        }


    private fun getContactFromCursor(c: Cursor) = c.use { cursor ->
        MyLog.d(TAG, "cursor columns count${cursor.columnCount}")

        if (cursor.moveToFirst() == false) {
            dynamicString("Can't import this contact.").inSnackbar().showMsg()
            return@use
        }

        val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val nameIndex =
            cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY)


        val name = cursor.getStringOrNull(nameIndex)
        val phone = cursor.getStringOrNull(phoneIndex)

        MyLog.d(TAG, "name to phone: $name, $phone")

        if (phone == null) {
            dynamicString("Can't import ${name ?: "this contact"}, try to copy it then paste here.")
                .inToast(duration = Toast.LENGTH_LONG)
                .showMsg()
            return@use
        }

        viewModelState.update {
            it.copy(query = phone, hint = name)
        }
    }


    private fun getSchedule(scheduleId: Long) {
        getSchedule.get().invoke(scheduleId = ScheduleId(scheduleId))
            .subscribe { schedule ->
                val (query, type, qHint) = schedule.searchQuery
                viewModelState.update {
                    it.copy(isLoading = false, query = query, queryType = type, hint = qHint)
                }
            }
    }


    private fun editSchedule(query: String, onEdited: () -> Unit) {
        val schedule = _schedule2.value ?: return
        val newSearchQuery = schedule.searchQuery.copy(query)


        scheduleEdit.get().invoke(scheduleId = schedule.wrappedId, newQuery = newSearchQuery)
            .subscribe({ onEdited(); MyLog.d(TAG, "Edit Completed", logThreadName = true) },
                { MyLog.e(TAG, "Edit Error $it") })
    }


    private fun errorHandler(err: InvalidSearchQueryException) {
        when (err) {
            is InvalidSearchQueryException -> dynamicString(err.message ?: "Invalid").inToast()
                .showMsg()
        }
    }

    private fun UserCommunicate.showMsg() = viewModelState.update { prev ->
        prev.copy(errorMessages = prev.errorMessages + this)
    }

    companion object {
        private const val TAG = "AddScheduleViwModel_Rob"
    }
}