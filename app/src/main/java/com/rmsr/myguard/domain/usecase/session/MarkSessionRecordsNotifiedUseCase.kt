package com.rmsr.myguard.domain.usecase.session

import com.rmsr.myguard.domain.entity.ScanRecord
import com.rmsr.myguard.domain.entity.Session
import com.rmsr.myguard.domain.entity.SessionId
import com.rmsr.myguard.domain.repository.ScanRecordsRepository
import com.rmsr.myguard.utils.MyLog
import com.rmsr.myguard.utils.component1
import com.rmsr.myguard.utils.component2
import com.rmsr.myguard.utils.onEmptyListComplete
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class MarkSessionRecordsNotifiedUseCase @Inject constructor(
    private val recordsRepository: ScanRecordsRepository,
) {
    private val TAG = "Rob_MarkSessionNotifiedUse"

    /**
     * Mark all existed [ScanRecord]s created in specified [Session] as notified
     * which means that user know about there existence or brief about their content.
     *
     * So when consumer ask about [Session] new records - notified items will not appear again.
     *
     * This use case is not aware if this session [ScanRecord]s already retrieved before or not.
     *
     * @param sessionId that all its records will updated.
     */
    operator fun invoke(sessionId: SessionId): Completable {
        return recordsRepository.getSessionRecords(sessionId)
            .map { it.filterNot { it.recordInfo.userNotified } }
            .onEmptyListComplete()

            .timestamp()
            .map { (time, scanRecords) ->

                return@map scanRecords.map { record ->
                    val recordInfo = record.recordInfo.copy(userNotified = true, notifyTime = time)
                    record.copy(recordInfo = recordInfo)
                }
            }

            .map { it.toSet() }
            .flatMapCompletable(recordsRepository::updateScanRecords)
            .doOnComplete { MyLog.d(TAG, "marked notified success", logThreadName = true) }

    }

    @Deprecated("pass SessionId for id parameter instead of Long.")
    operator fun invoke(sessionId: Long): Completable {
        return recordsRepository.sessionRecordsNotified(sessionId = sessionId)
            .doOnComplete { MyLog.d(TAG, "marked notified success", logThreadName = true) }

    }
}

