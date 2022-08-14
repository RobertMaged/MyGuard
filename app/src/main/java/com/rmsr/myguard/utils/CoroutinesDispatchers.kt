package com.rmsr.myguard.utils

import kotlinx.coroutines.CoroutineDispatcher

interface CoroutinesDispatchers {
    fun main(): CoroutineDispatcher
    fun io(): CoroutineDispatcher
    fun default(): CoroutineDispatcher
}

