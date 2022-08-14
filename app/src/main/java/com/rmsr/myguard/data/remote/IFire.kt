package com.rmsr.myguard.data.remote

import com.google.firebase.auth.FirebaseUser
import com.rmsr.myguard.BuildConfig
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface IFire {

    val authHeaders: Map<String, String>
    val pinners: Array<String>

    fun ensureUserSignedIn(): Completable
    fun initAppApis(): Completable

    fun getUser(): Single<FirebaseUser> =
        throw NotImplementedError("Implement ${BuildConfig.BUILD_TYPE} variant Firebase project first")
}