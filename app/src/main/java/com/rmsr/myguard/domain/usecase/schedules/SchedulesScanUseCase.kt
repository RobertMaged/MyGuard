package com.rmsr.myguard.domain.usecase.schedules

import com.rmsr.myguard.domain.entity.*
import com.rmsr.myguard.domain.repository.BreachRepository
import com.rmsr.myguard.domain.repository.ScanRecordsRepository
import com.rmsr.myguard.domain.repository.ScheduleRetrieveRepo
import com.rmsr.myguard.domain.repository.SessionRepository
import com.rmsr.myguard.domain.utils.HistoryMode
import com.rmsr.myguard.utils.component1
import com.rmsr.myguard.utils.component2
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Notification
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import kotlin.math.roundToLong

class SchedulesScanUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val scheduleRetrieve: ScheduleRetrieveRepo,
    private val breachRepository: BreachRepository,
    private val scanRecordsRepository: ScanRecordsRepository,
//    private val errorHandler: ErrorHandler,//TODO,
) {

    private data class SessionTracking(
        val created: Long = System.currentTimeMillis(),
        var startTime: Long = 0,
        var requestStartTime: Long = 0,
        var requestEndTime: Long = 0,
        val perRequestTime: MutableList<Long> = mutableListOf(0L),
        var endTime: Long = 0,
    )

    private val sessionTracking = SessionTracking()

    /**
     * @return session id
     */
    operator fun invoke(): Maybe<SessionId> {
        //subscribe to source in database to get schedules.
        return getSavedSchedules()

            //start online scanning and get result.
            .flatMapSingle { schedules ->
                scanSchedules(scheduleList = schedules)
            }

            //save the result to database then return Session id case needed.
            .flatMapSingle { scheduleToBreachesMap: Map<ScheduleId, List<BreachId>> ->
                saveRecords(result = scheduleToBreachesMap)
            }
    }


    /**
     * @return all schedules stored in database need to be scanned.
     */
    private fun getSavedSchedules(): Maybe<List<Schedule>> {
        return scheduleRetrieve.getAllSchedules()
            .doOnSubscribe { sessionTracking.startTime = System.currentTimeMillis() }
    }

    /**
     * @return map of schedule Id to breachesId list.
     */
    private fun scanSchedules(
        scheduleList: List<Schedule>,
    ): Single<Map<ScheduleId, List<BreachId>>> {

        //iterate over every schedule to start online scan.
        return Observable.fromIterable(scheduleList)

            .doOnSubscribe { sessionTracking.requestStartTime = System.currentTimeMillis() }

            //subscribe to search source and combine breach List result to its schedule.
            .concatMapMaybe { schedule ->
                return@concatMapMaybe searchForLeaksIds(schedule.searchQuery)

                    .map { resultBreaches: List<BreachId> ->
                        //Pair of
                        return@map ScheduleId(schedule.id) to resultBreaches
                    }
            }

            //combine all schedules each with its result in one map.
            .reduce(mutableMapOf<ScheduleId, List<BreachId>>(),
                { scheduleBreachesMap, (scheduleId, breachIds) ->
                    scheduleBreachesMap[scheduleId] = breachIds
                    return@reduce scheduleBreachesMap
                })

            //end of schedules online search, record time and return resulted Map immutable.
            .timestamp()
            .map { (time, schedulesMutableMap) ->
                sessionTracking.requestEndTime = time
                return@map schedulesMutableMap.toMap()
            }

    }

    private fun searchForLeaksIds(query: SearchQuery): Maybe<List<BreachId>> {
        return breachRepository.searchForLeaksIds(query, historyMode = HistoryMode.DISABLED)
            //just keep tracking for every request taken time to compute an average later.
            .materialize()
            .timeInterval()

            .map { (time, notification: Notification<List<BreachId>>) ->// it: Timed<Notification<List<BreachId>>> ->
                //errors request time not tracked
                if (notification.isOnError.not())
                    sessionTracking.perRequestTime.add(time)

                return@map notification
            }
            .dematerialize { it }
    }

    /**
     * init new session to get its id, wrapped in [SessionId].
     */
    private fun initSession(): Single<SessionId> =
        sessionRepository.initNewScanSession()

    /**
     * @param result map of schedule Id to breachesId list.
     * @return session id
     */
    private fun saveRecords(result: Map<ScheduleId, List<BreachId>>): Single<SessionId> {
        // init new session to get its id.
        return initSession()

            //save scanned schedules in this session.
            .flatMap { sessionId ->
                // FIXME: schedules that have no leaks; not included in result Map.
                return@flatMap sessionRepository.saveSessionScannedSchedules(
                    sessionId = sessionId,
                    schedulesIds = result.keys
                )
                    .toSingleDefault(sessionId)
            }

            //convert results to scan records (which contains relations between schedules, breaches and session),
            //to load it later to Session.
            .map { sessionId ->
                val resultRecords = convertResultToRecordList(sessionId, result = result)

                //PairOf (SessionId to List<ScanRecord>)
                return@map sessionId to resultRecords
            }

            //Every thing is done now and session completed, record end time and fill tracking data and result records to session object.
            .timestamp()
            .map { (time, pair: Pair<SessionId, List<ScanRecord>>) ->
                sessionTracking.endTime = time

                val session = loadDataToSession(
                    sessionId = pair.first,
                    trackingInfo = sessionTracking,
                )
                return@map session to pair.second
            }

            //save the Scan Session and its all done.
            .flatMap { (session, scanRecords) ->
                sessionRepository.updateSession(scanSession = session)
                    .mergeWith(scanRecordsRepository.createScanRecords(scanRecords.toSet()))

                    .toSingleDefault(session.wrappedId)
            }

    }

    /**
     * Converts resulted mapOf<[scheduleId, breachIdList]> to list of [ScanRecord].
     */
    private fun convertResultToRecordList(
        sessionId: SessionId,
        result: Map<ScheduleId, List<BreachId>>,
    ): List<ScanRecord> {

        return result.flatMap { entry ->
            entry.value.map { breachId ->
                ScanRecord(
                    identifiers = ScanRecord.Identifiers(
                        scheduleId = entry.key,
                        breachId = breachId,
                        sessionId = sessionId
                    ),
                    recordInfo = ScanRecord.RecordInfo(
                        userNotified = false,
                        notifyTime = 0,
                        userAcknowledged = false,
                        acknowledgeTime = 0
                    )
                )
            }
        }
    }

    /**
     * @return [Session] ready to be saved.
     */
    private fun loadDataToSession(
        sessionId: SessionId,
        trackingInfo: SessionTracking,
//        resultRecords: List<ScanRecord>,
    ): Session = Session(
        id = sessionId.value,
        userRespond = false,
        sessionInfo = Session.SessionInfo(
            createdTime = trackingInfo.created,
            startTime = trackingInfo.startTime,
            requestStartTime = trackingInfo.requestStartTime,
            requestEndTime = trackingInfo.requestEndTime,
            requestsAvgTime = trackingInfo.perRequestTime.average().roundToLong(),
            endTime = trackingInfo.endTime,
        )
    )

    companion object {
        /**
         * API is limited to one per every 1500 milliseconds,
         * 100 milliseconds is added to make safe request without exceeding limit.
         */
        const val API_BREACHES_RATE_LIMIT_MILLIS: Long = 3600
    }
}