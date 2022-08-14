package com.rmsr.myguard.domain.usecase.breaches

import com.rmsr.myguard.domain.entity.Breach
import com.rmsr.myguard.domain.entity.SessionId
import com.rmsr.myguard.domain.repository.BreachRepository
import com.rmsr.myguard.domain.repository.ScanRecordsRepository
import com.rmsr.myguard.domain.repository.SessionRepository
import com.rmsr.myguard.utils.MyLog
import io.reactivex.rxjava3.core.Maybe
import java.util.*
import javax.inject.Inject

/**
 * Get specific session breaches witch user didn't notified about them yet.
 */
class GetSessionUnNotifiedLeaksUseCase @Inject constructor(
    private val breach: BreachRepository,
    private val record: ScanRecordsRepository,
    private val session: dagger.Lazy<SessionRepository>,
) {
    private val TAG = "Rob_GetSessionUnNotLeak"

    /**
     * Return 'last' session breaches that user didn't notified about.
     *
     * @return UnNotified breaches of last session.
     * @see #invoke(SessionId)
     */
    operator fun invoke(): Maybe<List<Breach>> {
        return session.get().getLastSession()
            .flatMap { this.invoke(it.wrappedId) }
    }

    /**
     * Return session breaches that user didn't notified about.
     *
     * @param sessionId to get this session un Notified breaches.
     * @return UnNotified breaches of this session.
     * @see invoke
     */
    operator fun invoke(sessionId: SessionId): Maybe<List<Breach>> {
        return record.getSessionRecords(sessionId)
            .mapOptional {
                val unNotifiedRecords = it.filter { it.recordInfo.userNotified.not() }
                return@mapOptional when {
                    unNotifiedRecords.isNotEmpty() -> Optional.of(unNotifiedRecords)
                    else -> Optional.empty()
                }
            }
            .flatMap {
                val breachesIds = it.map { it.identifiers.breachId }
                breach.getBreachesByIds(breachesIds)
            }
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
}