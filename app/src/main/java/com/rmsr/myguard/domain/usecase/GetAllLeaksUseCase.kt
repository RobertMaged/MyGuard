package com.rmsr.myguard.domain.usecase

import com.rmsr.myguard.domain.entity.Breach
import com.rmsr.myguard.domain.entity.ScheduleId
import com.rmsr.myguard.domain.entity.SessionId
import com.rmsr.myguard.domain.repository._ScheduleLeaks
import com.rmsr.myguard.domain.repository._SessionLeaks
import com.rmsr.myguard.utils.logTimeInterval
import io.reactivex.rxjava3.core.Maybe
import javax.inject.Inject

@Deprecated("")
class GetAllLeaksUseCase @Inject constructor(
    private val scheduleLeaks: _ScheduleLeaks,
    private val sessionLeaks: _SessionLeaks
) {
    private val TAG = "Rob_GetAllLeaksUseCase"

    @Deprecated("Use GetScheduleAllLeaks instead.")
    operator fun invoke(scheduleId: ScheduleId): Maybe<List<Breach>> =
        scheduleLeaks.getScheduleAllLeaks(scheduleId)
            .logTimeInterval(TAG, "old ScheduleAllLeaK")

    @Suppress("not used")
    operator fun invoke(sessionId: SessionId): Maybe<List<Breach>> =
        sessionLeaks.getSessionAllLeaks(sessionId)
}