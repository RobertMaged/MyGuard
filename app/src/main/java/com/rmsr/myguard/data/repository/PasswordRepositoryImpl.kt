package com.rmsr.myguard.data.repository

import com.rmsr.myguard.data.remote.PasswordsRemoteDataSource
import com.rmsr.myguard.domain.entity.errors.ErrorEntity
import com.rmsr.myguard.domain.repository.PasswordRepository
import com.rmsr.myguard.utils.RxSchedulers
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PasswordRepositoryImpl @Inject constructor(
    private val passwordApi: PasswordsRemoteDataSource,
    private val rxSchedulers: RxSchedulers,
) : PasswordRepository {

    override fun findPasswordLeaksCount(passwordHash: String): Maybe<Int> {
        val (first5HashChars, restOfHash) = passwordHash splitAt 5

        return passwordApi.getHashSuffixes(first5hashChars = first5HashChars)
            .subscribeOn(rxSchedulers.io())

            .onErrorResumeNext {
                Single.error(
                    when (it) {
                        is IOException -> ErrorEntity.NoNetwork
                        is HttpException ->
                            ErrorEntity.ApiError(
                                responseCode = it.code(),
                                message = it.message(),
                                cause = it
                            )
                        else -> ErrorEntity.UnKnownError(it.message ?: "Unknown password error", it)
                    }
                )
            }

            .map { hashSuffixes: String -> getHashLeaksCount(restOfHash, hashSuffixes) }

            //0 means no leaks found for password hash.
            .flatMapMaybe { count ->
                if (count > 0) Maybe.just(count) else Maybe.empty()
            }
    }

    private infix fun String.splitAt(inclusiveIndex: Int): Pair<String, String> {
        return this.take(inclusiveIndex) to this.drop(inclusiveIndex)
    }

    private fun getHashLeaksCount(restOfHash: String, hashSuffixes: String): Int {
//        response example:
//        "00727778DA2BBDA429A2F4CB3546D4E804F:123 \n"
//        "00727778DA2BBDA429A2F4CB3546D4R6PP9:1049 \n"

        return hashSuffixes.substringAfter(
            delimiter = restOfHash + ':',
            missingDelimiterValue = "null"
        )

            .takeIf { it != "null" }

            //take numbers after ':' delimiter
            ?.takeWhile { it.isDigit() }

            ?.toInt() ?: 0
    }
}