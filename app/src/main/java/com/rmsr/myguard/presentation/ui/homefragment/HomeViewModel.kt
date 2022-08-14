package com.rmsr.myguard.presentation.ui.homefragment

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmsr.myguard.R
import com.rmsr.myguard.domain.entity.Breach
import com.rmsr.myguard.domain.entity.QueryType
import com.rmsr.myguard.domain.entity.SearchQuery
import com.rmsr.myguard.domain.entity.errors.InvalidSearchQueryException
import com.rmsr.myguard.domain.usecase.breaches.SearchForLeaksUseCase
import com.rmsr.myguard.domain.usecase.breaches.SortBreachesUseCase
import com.rmsr.myguard.presentation.util.*
import com.rmsr.myguard.utils.MyLog
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.MaybeObserver
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.flow.*
import java.time.format.DateTimeFormatter
import javax.inject.Inject


private data class HomeViewModelState(
    val isLoading: Boolean = false,
    val resultReady: Boolean = false,
    val breaches: List<Breach> = emptyList(),
    val query: String = "",
    val queryType: QueryType = QueryType.EMAIL,
    val breachSort: BreachSortType = BreachSortType.DATE_DESCENDING,
    val errorMessages: List<UserCommunicate> = emptyList(),
) {
    fun toComponentsState() = HomeUiResourcesState(
        query, queryType, breachSort,
    )

    fun toUiState(): HomeUiState {
        val uiItems = breaches.map { breach ->
            BreachItemUiState(
                id = breach.id,
                title = breach.title,
                logoUrl = breach.metadata.logoUrl,
                discoveredDate = breach.leakInfo.discoveredDate.format(
                    DateTimeFormatter.ofPattern("MMM. yyyy")
                ),
                compromisedData = breach.leakInfo.compromisedData.toString().trim('[', ']'),
                description = breach.leakInfo.description,
            )
        }
        return HomeUiState(
            isLoading = isLoading,
            resultReady = resultReady,
            isPwned = breaches.isNotEmpty(),
            breachesItems = uiItems,
            errorMessages = errorMessages
        )
    }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val searchBreaches: SearchForLeaksUseCase,
    private val sortBreaches: SortBreachesUseCase,
) : ViewModel() {


    private val viewModelState = MutableStateFlow(restoreStateIfExist() ?: HomeViewModelState())

    var query: String = viewModelState.value.query
        set(value) {
            if (field == value) return
            field = value
            viewModelState.update { it.copy(query = value, resultReady = false) }
        }


    val uiState: StateFlow<HomeUiState> by lazy {
        viewModelState
            .map { it.toUiState() }
            .distinctUntilChanged()
            .onEach { MyLog.d(TAG, "ui state: $it") }
            .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())
    }

    val componentsState: StateFlow<HomeUiResourcesState> by lazy {
        viewModelState
            .map { it.toComponentsState() }
            .distinctUntilChanged()
            .onEach { MyLog.d(TAG, "ui state: $it") }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                viewModelState.value.toComponentsState()
            )
    }

    init {
        savedStateHandle.setSavedStateProvider(SAVED_STATE_MAIN_BUNDLE) {
            val uiResourcesState = viewModelState.value.toComponentsState()

            bundleOf(UI_RESOURCES_STATE_PARCELABLE to uiResourcesState)
        }
    }

    private val disposables by lazy { CompositeDisposable() }


    private val leaksRequestObserver by lazy {
        object : MaybeObserver<List<Breach>> {

            //dispose any new subscription until if previous one is not finish yet.
            override fun onSubscribe(d: Disposable) =
                if (viewModelState.value.isLoading) {
                    d.dispose()
                    resourceString(R.string.home_frag_wait_breaches_loading).inToast().showMsg()

                } else {
                    disposables.add(d)
                    viewModelState.update { it.copy(isLoading = true, resultReady = false) }
                }


            //found leaks
            override fun onSuccess(breaches: List<Breach>) = viewModelState.update {
                val sortedBreaches = sortBreachesList(breaches, it.breachSort)
                it.copy(isLoading = false, resultReady = true, breaches = sortedBreaches)
            }


            override fun onError(error: Throwable) {
                val errorToast = dynamicString(error.message ?: "Unknown error happened").inToast()
                viewModelState.update { prev ->
                    prev.copy(
                        errorMessages = prev.errorMessages + errorToast,
                        isLoading = false, resultReady = false
                    )
                }
            }


            //empty response
            override fun onComplete() = viewModelState.update {
                it.copy(isLoading = false, breaches = emptyList(), resultReady = true)
            }


        }
    }

/*
    fun onHistoryReady() {
        queryHolder
            .debounce(800L, TimeUnit.MILLISECONDS)
            .flatMapMaybe { history.invoke(it) }
            .subscribe({ h -> viewModelState.update { it.copy(history = h) } },
                {},
                { viewModelState.update { it.copy(history = emptyList()) } }, disposables
            )
    }
*/

    fun search() = with(viewModelState.value) {
        when (queryType) {
            QueryType.EMAIL -> getBreachesByAccount(query)
            QueryType.Phone -> getBreachesByPhone(query)
            else -> getBreachByDomainOrName(query)
        }
    }

    fun setCategory(queryType: QueryType) =
        viewModelState.update { it.copy(queryType = queryType, resultReady = false) }

    fun setSort(sort: BreachSortType) = viewModelState.update {
        val sortedBreaches = sortBreachesList(it.breaches, sort)
        it.copy(breachSort = sort, breaches = sortedBreaches, resultReady = true)
    }

    fun endIconClicked(onInfoRequired: (infoMessage: UiText) -> Unit) {
        if (viewModelState.value.query.isEmpty())
            onInfoRequired(viewModelState.value.toComponentsState().infoRes)
        else
            viewModelState.update { it.copy(query = "") }
    }


    fun userMessageShown(msgId: Long) = viewModelState.update {
        val messages = it.errorMessages.filterNot { it.id == msgId }
        it.copy(errorMessages = messages)
    }


    fun getBreachesByAccount(query: String) {

        searchBreaches.searchForLeaks(SearchQuery.Email(query))

            .subscribe(leaksRequestObserver)
    }


    fun getBreachesByPhone(phoneNumber: String) {

        searchBreaches.searchForLeaks(SearchQuery.Phone(phoneNumber))

            .subscribe(leaksRequestObserver)
    }


    fun getBreachByDomainOrName(domainOrName: String) {
        searchBreaches.searchForLeaks(SearchQuery.Domain(domainOrName))

            .onErrorResumeNext { error ->
                if (error is InvalidSearchQueryException.InvalidDomainException)
                    searchBreaches.searchForLeaks(SearchQuery.DomainName(domainOrName))
                else
                    Maybe.error(error)
            }
            .subscribe(leaksRequestObserver)
    }


    private fun restoreStateIfExist(): HomeViewModelState? {
        return savedStateHandle.get<Bundle>(SAVED_STATE_MAIN_BUNDLE)
            ?.getParcelable<HomeUiResourcesState>(UI_RESOURCES_STATE_PARCELABLE)
            ?.let {
                HomeViewModelState(
                    query = it.query,
                    queryType = it.queryType,
                    breachSort = it.breachSort
                )
            }
    }

    private fun sortBreachesList(breaches: List<Breach>, sort: BreachSortType): List<Breach> =
        when (sort) {
            BreachSortType.DATE_ASCENDING -> sortBreaches.byDateAscending(breaches)
            BreachSortType.DATE_DESCENDING -> sortBreaches.byDateDescending(breaches)
            BreachSortType.TITLE_ASCENDING -> sortBreaches.byTitleAscending(breaches)
            BreachSortType.TITLE_DESCENDING -> sortBreaches.byTitleDescending(breaches)
            BreachSortType.PWN_COUNT_ASCENDING -> sortBreaches.byPwnCountAscending(breaches)
            BreachSortType.PWN_COUNT_DESCENDING -> sortBreaches.byPwnCountDescending(breaches)
        }


    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    private fun UserCommunicate.showMsg() = viewModelState.update { prev ->
        prev.copy(errorMessages = prev.errorMessages + this)
    }

    companion object {
        private const val TAG = "Rob_HomeViewModel"
        private const val SAVED_STATE_MAIN_BUNDLE = "state"
        private const val UI_RESOURCES_STATE_PARCELABLE = "holder"
    }

}

