package com.rmsr.myguard.domain.usecase

import com.rmsr.myguard.domain.entity.Breach
import com.rmsr.myguard.domain.entity.ScheduleId
import com.rmsr.myguard.domain.entity.SessionId
import com.rmsr.myguard.domain.repository._ScheduleLeaks
import com.rmsr.myguard.domain.repository._SessionLeaks
import com.rmsr.myguard.utils.MyLog
import com.rmsr.myguard.utils.logTimeInterval
import io.reactivex.rxjava3.core.Maybe
import javax.inject.Inject

@Deprecated("")
class GetLatestLeaksUseCase @Inject constructor(
    private val scheduleLeaks: _ScheduleLeaks,
    private val sessionLeaks: _SessionLeaks,
) {
    private val TAG = "Rob_GetLatestLeaksUseCase"

    @Deprecated("Use GetScheduleUnSeenLeaks instead.")
    operator fun invoke(scheduleId: ScheduleId): Maybe<List<Breach>> =
        scheduleLeaks.getScheduleNewLeaks(scheduleId)
            .logTimeInterval(TAG, "old ScheduleNewLeaK")

    @Deprecated("Use GetSessionUnNotifiedLeaks instead.")
    operator fun invoke(sessionId: SessionId): Maybe<List<Breach>> =
        sessionLeaks.getSessionNewLeaks(sessionId)
            .doOnSuccess { newLeaks ->
                MyLog.d(
                    TAG,
                    "getting new leaks of sessionId: $sessionId - to newLeaks: $newLeaks.",
                    logThreadName = true
                )
            }
            .doOnComplete {
                MyLog.d(
                    TAG,
                    "getting new leaks with sessionId: $sessionId - DONE with no new leaks. no problem.",
                    logThreadName = true
                )
            }
            .doOnError {
                MyLog.w(
                    TAG,
                    "getting new leaks with sessionId: $$sessionId with some error: ${it.message}.",
                    logThreadName = true, tr = it
                )
            }

}