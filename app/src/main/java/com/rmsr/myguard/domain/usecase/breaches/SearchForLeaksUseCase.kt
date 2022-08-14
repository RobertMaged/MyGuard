package com.rmsr.myguard.domain.usecase.breaches

import com.rmsr.myguard.domain.entity.Breach
import com.rmsr.myguard.domain.entity.BreachId
import com.rmsr.myguard.domain.entity.SearchQuery
import com.rmsr.myguard.domain.entity.errors.ErrorEntity
import com.rmsr.myguard.domain.entity.errors.InvalidSearchQueryException
import com.rmsr.myguard.domain.repository.BreachRepository
import com.rmsr.myguard.domain.repository.ErrorHandler
import com.rmsr.myguard.domain.usecase.ValidateSearchQueryUseCase
import com.rmsr.myguard.domain.utils.Validator
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.MaybeObserver
import javax.inject.Inject


class SearchForLeaksUseCase @Inject constructor(
    private val repository: BreachRepository,
    private val queryValidation: ValidateSearchQueryUseCase,
    private val errorHandler: ErrorHandler,

    ) {
    private val TAG = "Rob_SearchBreachUseCase"
    private val repositoryName: String = repository.javaClass.simpleName

    /**
     * Get all leaks this [searchQuery] appears in.
     *
     * [MaybeObserver.onError] will be called If entered query is not valid with a [InvalidSearchQueryException],
     * if this behavior not preferred see [searchForLeaksWrapped].
     *
     * @return [MaybeObserver.onSuccess] with found [Breach]s,
     *  or [MaybeObserver.onComplete] if no leaks found for this [SearchQuery].
     *
     * @throws InvalidSearchQueryException if entered [searchQuery] is not valid.
     *
     * @see searchForLeaksWrapped
     */
    fun searchForLeaks(searchQuery: SearchQuery): Maybe<List<Breach>> =
        validateSearchQuery(searchQuery).fold(
            onSuccess = { query -> runWithErrorGuard { repository.searchForLeaks(query) } },
            onFailure = { thr -> Maybe.error(thr) }
        )


    /**
     * Get all leaks this [searchQuery] appears in and wrap it in [Result.success].
     *
     *
     * [MaybeObserver.onSuccess] will be called If entered query is not valid with a [InvalidSearchQueryException] wrapped in [Result.failure],
     * if this behavior not preferred see [searchForLeaks].
     *
     * @return [MaybeObserver.onSuccess] with found [Breach]s wrapped in [Result.success],
     *      [MaybeObserver.onSuccess] with exception if search query is invalid wrapped in [Result.failure],
     *  or [MaybeObserver.onComplete] if no leaks found for this [SearchQuery].
     *
     * @see searchForLeaks
     */
    fun searchForLeaksWrapped(searchQuery: SearchQuery): Maybe<Result<List<Breach>>> =
        validateSearchQuery(searchQuery).fold(
            onSuccess = { query -> runAndWrapResult { repository.searchForLeaks(query) } },
            onFailure = { thr -> Maybe.just(Result.failure(thr)) }
        )


    /**
     * Get all leaks id this [searchQuery] appears in.
     *
     * [MaybeObserver.onError] will be called If entered query is not valid with a [InvalidSearchQueryException],
     * if this behavior not preferred see [searchForLeaksIdsWrapped].
     *
     * @return [MaybeObserver.onSuccess] with found [BreachId]s,
     *  or [MaybeObserver.onComplete] if no leaks found for this [SearchQuery].
     *
     * @throws InvalidSearchQueryException if entered [searchQuery] is not valid.
     *
     * @see searchForLeaksIdsWrapped
     */
    fun searchForLeaksIds(searchQuery: SearchQuery): Maybe<List<BreachId>> =
        validateSearchQuery(searchQuery).fold(
            onSuccess = { query -> runWithErrorGuard { repository.searchForLeaksIds(query) } },
            onFailure = { thr -> Maybe.error(thr) }
        )


    /**
     * Get all leaks id this [searchQuery] appears in and wrap it in [Result.success].
     *
     *
     * [MaybeObserver.onSuccess] will be called If entered query is not valid with a [InvalidSearchQueryException] wrapped in [Result.failure],
     * if this behavior not preferred see [searchForLeaksIds].
     *
     * @return [MaybeObserver.onSuccess] with found [BreachId]s wrapped in [Result.success],
     *      [MaybeObserver.onSuccess] with exception if search query is invalid wrapped in [Result.failure],
     *  or [MaybeObserver.onComplete] if no leaks found for this [SearchQuery].
     *
     * @see searchForLeaks
     */
    fun searchForLeaksIdsWrapped(searchQuery: SearchQuery): Maybe<Result<List<BreachId>>> =
        validateSearchQuery(searchQuery).fold(
            onSuccess = { query -> runAndWrapResult { repository.searchForLeaksIds(query) } },
            onFailure = { thr -> Maybe.just(Result.failure(thr)) }
        )


    private fun validateSearchQuery(
        searchQuery: SearchQuery,
    ): Result<SearchQuery> =
        when (val result = queryValidation.invoke(searchQuery, autoCorrect = true)) {
            is Validator.Invalid -> Result.failure(result.exception)
            is Validator.AutoCorrected -> Result.success(result.correctedData)
            Validator.Valid -> Result.success(searchQuery)
        }

    private inline fun <T> runWithErrorGuard(source: () -> Maybe<T>): Maybe<T> {
        return source()
            .onErrorResumeNext {
                Maybe.error(errorHandler.getError(it))
            }
    }

    private inline fun <T> runAndWrapResult(source: () -> Maybe<T>): Maybe<Result<T>> {
        return source()
            .map {
                Result.success(it)
            }
            .onErrorResumeNext {
                when (it) {
                    is ErrorEntity.ValidationError -> Maybe.just(Result.failure(it))
                    else -> Maybe.error(errorHandler.getError(it))
                }
            }
    }

}