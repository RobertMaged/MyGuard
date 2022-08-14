package com.rmsr.myguard.data.remote

import com.rmsr.myguard.BuildConfig
import com.rmsr.myguard.utils.getValue
import com.rmsr.myguard.utils.setValue
import io.reactivex.rxjava3.core.Completable
import java.util.concurrent.atomic.AtomicReference


object Fire : IFire {
    private const val TAG = "Rob_Fire"

    override val authHeaders: Map<String, String>
        get() = mapOf(
            "hibp-api-key" to tokens.api,
            "user-agent" to tokens.agent
        )

    override val pinners: Array<String>
        get() = tokens.pin.toTypedArray()

    override fun ensureUserSignedIn(): Completable = Completable.complete()

    override fun initAppApis(): Completable {
        calcTokens(BuildConfig.HIBP_AUTH, BuildConfig.HIBP_PINS)
        return Completable.complete()
    }

    private var tokens by AtomicReference(Tokens())

    private data class Tokens(
        val api: String = "",
        val agent: String = "",
        val pin: List<String> = listOf("", "", "")
    )


    private fun calcTokens(init: String, pin: String) {
        val t = init.split('/')

        val p = pin.split('=')

        tokens = Tokens(t[0], t[1], List(3) { "sha256/${p[it]}=" })
    }

}