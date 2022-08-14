package com.rmsr.myguard.domain.usecase

import com.rmsr.myguard.domain.utils.ConnectionMonitor
import com.rmsr.myguard.domain.utils.NetworkStatus
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class ObserveConnectionStateUseCase @Inject constructor(
    private val connectionMonitor: ConnectionMonitor
) {

    val stateValue: NetworkStatus
        get() = connectionMonitor.status

    operator fun invoke(): Observable<NetworkStatus> = connectionMonitor.observeState()
}