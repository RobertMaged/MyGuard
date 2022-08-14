package com.rmsr.myguard.domain.usecase.session

import com.rmsr.myguard.domain.entity.Session
import com.rmsr.myguard.domain.entity.SessionId
import com.rmsr.myguard.domain.repository.SessionRepository
import com.rmsr.myguard.utils.MyLog
import com.rmsr.myguard.utils.RxSchedulers
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class MarkSessionRespondedUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val rxSchedulers: RxSchedulers,
) {
    private val TAG = "Rob_MarkSessionResponded"

    /**
     * Mark [Session] as responded which means that
     * user interested in details when he notified about that session.
     *
     * @param sessionId Id of [Session] that will be marked as responded.
     */
    operator fun invoke(sessionId: SessionId): Completable {
        return sessionRepository.getSession(sessionId)
            .subscribeOn(rxSchedulers.io())
            .map { it.copy(userRespond = true) }
            .flatMapCompletable(sessionRepository::updateSession)
            .doOnComplete { MyLog.d(TAG, "marked session responded success", logThreadName = true) }
    }

    @Deprecated("pass SessionId for id parameter instead of Long.")
    operator fun invoke(sessionId: Long): Completable {
        return sessionRepository.markSessionUserResponded(sessionId = sessionId)
    }
}