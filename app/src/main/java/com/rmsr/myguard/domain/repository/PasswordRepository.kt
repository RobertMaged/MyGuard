package com.rmsr.myguard.domain.repository

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.MaybeObserver

interface PasswordRepository {
    /**
     * Search for password leaks and if exist return found leaks count.
     *
     * @param passwordHash password to search encrypted in 'SHA-1' hash.
     *
     * @return [MaybeObserver.onSuccess] with number of leaks found,
     *      or [MaybeObserver.onComplete] when password is not leaked.
     */
    fun findPasswordLeaksCount(passwordHash: String): Maybe<Int>
}