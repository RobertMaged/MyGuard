package com.rmsr.myguard.domain.utils

import io.reactivex.rxjava3.core.Observable

interface ConnectionMonitor {
    val status: NetworkStatus

    fun observeState(): Observable<NetworkStatus>
}

sealed class NetworkStatus {
    object Online : NetworkStatus()
    object Offline : NetworkStatus()
    object Checking : NetworkStatus()
    object VPN : NetworkStatus()
}