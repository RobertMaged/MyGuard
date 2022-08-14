package com.rmsr.myguard.domain.usecase.schedules

import com.rmsr.myguard.domain.entity.*
import com.rmsr.myguard.domain.repository.*
import com.rmsr.myguard.utils.MyLog
import com.rmsr.myguard.utils.logTimeInterval
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject


class ObserveSchedulesStuffUseCase @Inject constructor(
    private val scheduleRepo: ScheduleRetrieveRepo,
    private val leaks: BreachRepository,
    private val sessions: SessionRepository,
    private val scanRecords: ScanRecordsRepository,
    private val errorHandler: ErrorHandler,
) {
    private val repositoryName: String = scheduleRepo.javaClass.simpleName


    operator fun invoke(): Flowable<List<ScheduleStuff>> {
        return scheduleRepo.observeSchedules()
            .logTimeInterval(TAG, "$repositoryName: just schedules time}")

            .switchMapObserveSchedulesRecords()
            .logTimeInterval(TAG, "after getting schedules records time")

            .switchMapGetSchedulesStuff()
            .logTimeInterval(TAG, "after schedules stuff ready time")

            .map { schedulesStuff ->
                schedulesStuff.sortedByDescending { it.lastMassageTime }
            }
            .onErrorResumeNext {
                Flowable.error(errorHandler.getError(it))
            }
    }

    private fun Flowable<List<Schedule>>.switchMapObserveSchedulesRecords(): Flowable<Pair<List<Schedule>, List<ScanRecord>>> {

        return this.switchMap { schedules ->
            scanRecords.observeSchedulesRecords(schedules.map { it.wrappedId })
                .map { records ->
                    schedules to records
                }
        }
    }

    private fun Flowable<Pair<List<Schedule>, List<ScanRecord>>>.switchMapGetSchedulesStuff(): Flowable<List<ScheduleStuff>> {

        return this.switchMapSingle { (schedules, records) ->

            return@switchMapSingle loadSessionsAndLeaksFromRecords(records)

                .map { (sessions, breaches) ->

                    val t = System.currentTimeMillis()

                    return@map concatDataInScheduleStuff(
                        allSchedules = schedules,
                        allSessions = sessions,
                        allBreaches = breaches,
                        allRecords = records
                    ).apply {
                        MyLog.d(TAG, "compute time= ${System.currentTimeMillis() - t}")
                    }
                }
        }


    }

    private fun loadSessionsAndLeaksFromRecords(records: List<ScanRecord>): Single<Pair<List<Session>, List<Breach>>> {
        val sessionsNeeded = hashSetOf<SessionId>()
        val breachesNeeded = hashSetOf<BreachId>()

        records.forEach {
            sessionsNeeded.add(it.identifiers.sessionId)
            breachesNeeded.add(it.identifiers.breachId)
        }

        return Single.zip(
            sessions.getSessions(sessionsIds = sessionsNeeded.toList())
                .logTimeInterval(TAG, "getting only sessions time")
                .defaultIfEmpty(emptyList()),

            leaks.getBreachesByIds(breachesIds = breachesNeeded.toList())
                .logTimeInterval(TAG, "getting only breaches time")
                .defaultIfEmpty(emptyList())

        ) { sessions, breaches ->

            return@zip sessions to breaches
        }
    }

    private fun concatDataInScheduleStuff(
        allSchedules: List<Schedule>, allSessions: List<Session>,
        allBreaches: List<Breach>, allRecords: List<ScanRecord>,
    ): List<ScheduleStuff> {
        val breachesMap: Map<BreachId, Breach> = allBreaches.associateBy { it.wrappedId }

        val sessionsMap: Map<SessionId, Session> = allSessions.associateBy { it.wrappedId }

        val scheduleToRecordsMap: Map<ScheduleId, List<ScanRecord>> =
            allRecords.groupBy { it.identifiers.scheduleId }

        return allSchedules.map { schedule ->
            val scheduleRecords = scheduleToRecordsMap[schedule.wrappedId]
                ?: return@map ScheduleStuff(
                    schedule = schedule,
                    leaks = LeaksFeed.EMPTY,
                    sessions = SessionsFeed.EMPTY,
                    scanRecords = emptyList()
                )

            val freshLeaks = hashSetOf<Breach>()
            val notifiedOnlyLeaks = hashSetOf<Breach>()
            val notifiedAndAcknowledgedLeaks = hashSetOf<Breach>()

            val scheduleLeaksFoundSessions = hashSetOf<Session>()

            scheduleRecords.forEach {
                scheduleLeaksFoundSessions += sessionsMap.getValue(it.identifiers.sessionId)


                val leak = breachesMap.getValue(it.identifiers.breachId)
                when {
                    it.recordInfo.userNotified && it.recordInfo.userAcknowledged -> notifiedAndAcknowledgedLeaks += leak
                    it.recordInfo.userNotified -> notifiedOnlyLeaks += leak
                    else -> freshLeaks += leak
                }

            }

            val lastLeaksFoundSession =
                scheduleLeaksFoundSessions.maxByOrNull { it.sessionInfo.createdTime }

            val lastSessionLeaksIds = scheduleRecords
                //lastLeaksFoundSession will never be null as long as scheduleRecords not empty
                .filter { it.identifiers.sessionId == lastLeaksFoundSession?.wrappedId }
                .map { it.identifiers.breachId }

            val lastSessionLeaks = hashSetOf<Breach>()
            breachesMap.filterKeys { it in lastSessionLeaksIds }
                .mapTo(lastSessionLeaks) { it.value }

            val leaksFeed = LeaksFeed(
                fresh = freshLeaks,
                notifiedOnly = notifiedOnlyLeaks,
                notifiedAndAcknowledged = notifiedAndAcknowledgedLeaks,
                lastFoundLeaks = lastSessionLeaks
            )

            val sessionsFeed = SessionsFeed(
                leaksFoundSessions = scheduleLeaksFoundSessions,
                lastLeaksFoundSession = lastLeaksFoundSession,
            )

            return@map ScheduleStuff(
                schedule = schedule,
                leaks = leaksFeed,
                sessions = sessionsFeed,
                scanRecords = scheduleRecords
            )
        }
    }

    companion object {
        private const val TAG = "ObserveSchedulesUseCase_Rob"
    }
}