package com.rmsr.myguard.data.repository

import com.rmsr.myguard.data.database.dao.ScanRecordDao
import com.rmsr.myguard.data.database.entity.ScheduleScanRecordEntity
import com.rmsr.myguard.data.mapper.ReversibleMapper
import com.rmsr.myguard.domain.entity.Breach
import com.rmsr.myguard.domain.entity.ScanRecord
import com.rmsr.myguard.domain.entity.ScheduleId
import com.rmsr.myguard.domain.entity.SessionId
import com.rmsr.myguard.domain.repository.BreachRepository
import com.rmsr.myguard.domain.repository.ScanRecordsRepository
import com.rmsr.myguard.domain.repository._ScheduleLeaks
import com.rmsr.myguard.domain.repository._SessionLeaks
import com.rmsr.myguard.utils.RxSchedulers
import com.rmsr.myguard.utils.onEmptyListComplete
import com.rmsr.myguard.utils.onEmptyMapComplete
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanRecordsRepositoryImpl @Inject constructor(
    private val scanRecordDao: ScanRecordDao,
    @Deprecated("Used in deprecated [_ScheduleLeaks, _SessionLeaks] implementations only.")
    private val breachRetrieve: BreachRepository,
    private val recordMapper: ReversibleMapper<ScheduleScanRecordEntity, ScanRecord>,
    private val rxSchedulers: RxSchedulers,
) : ScanRecordsRepository, _ScheduleLeaks, _SessionLeaks {


    override fun createScanRecords(scanRecords: Set<ScanRecord>): Completable {

        val insertionWay: Observable<out Iterable<ScanRecord>> =
            if (scanRecords.size < SQLITE_MAX_VARIABLE_NUMBER)
                Observable.just(scanRecords)
            else
                Observable.fromIterable(scanRecords)
                    .buffer(SQLITE_MAX_VARIABLE_NUMBER)


        return insertionWay
            .subscribeOn(rxSchedulers.io())

            .map { records -> recordMapper.mapReversed(records) }

            .flatMapCompletable(scanRecordDao::insert)
    }


    override fun updateScanRecords(scanRecords: Set<ScanRecord>): Completable {
        val insertionWay: Observable<out Iterable<ScanRecord>> =
            if (scanRecords.size < SQLITE_MAX_VARIABLE_NUMBER)
                Observable.just(scanRecords)
            else
                Observable.fromIterable(scanRecords)
                    .buffer(SQLITE_MAX_VARIABLE_NUMBER)


        return insertionWay
            .subscribeOn(rxSchedulers.io())

            .map { records -> recordMapper.mapReversed(records) }

            .flatMapCompletable(scanRecordDao::update)
    }


    override fun getSessionRecords(sessionId: SessionId): Maybe<List<ScanRecord>> {
        return scanRecordDao.getSessionRecords(sessionId.value)
            .subscribeOn(rxSchedulers.io())

            .onEmptyListComplete()

            .map { recordsEntities -> recordMapper.map(recordsEntities) }
    }


    override fun getScheduleRecords(scheduleId: ScheduleId): Maybe<List<ScanRecord>> {
        return scanRecordDao.getScheduleRecords(scheduleId.value)
            .subscribeOn(rxSchedulers.io())

            .onEmptyListComplete()

            .map { recordsEntities -> recordMapper.map(recordsEntities) }
    }


    override fun observeSchedulesRecords(schedulesIds: List<ScheduleId>): Flowable<List<ScanRecord>> {
        val ids: List<Long> = schedulesIds.map { it.value }

        return scanRecordDao.observeSchedulesRecords(schedulesIds = ids)
            .subscribeOn(rxSchedulers.io())

            .onBackpressureLatest()
            .distinctUntilChanged()

            .map { recordsEntities -> recordMapper.map(recordsEntities) }
    }

    override fun scheduleRecordsAcknowledged(scheduleId: ScheduleId): Completable {
        return scanRecordDao
            .setScheduleRecordsAcknowledged(scheduleId = scheduleId.value)
            .subscribeOn(rxSchedulers.io())
    }

    override fun sessionRecordsNotified(sessionId: Long): Completable {
        return scanRecordDao.updateRecordsNotified(sessionId = sessionId)
            .subscribeOn(rxSchedulers.io())
    }

    ///////////////Deprecated////////////////

    override fun getScheduleNewLeaks(scheduleId: ScheduleId): Maybe<List<Breach>> {
        return scanRecordDao.getScheduleUnacknowledgedBreachesIds(scheduleId = scheduleId.value)
            .subscribeOn(rxSchedulers.io())
            .onEmptyListComplete()
            .flatMap { breachRetrieve.getBreachesByIds(it) }
    }

    override fun getSchedulesNewLeaks(schedulesIds: List<Long>): Maybe<Map<Long, List<Breach>>> {
        return scanRecordDao.getSchedulesUnacknowledgedBreachesIds(schedulesIds = schedulesIds)
            .onEmptyMapComplete()
            .subscribeOn(rxSchedulers.io())

            .flatMap { myMap ->
                val neededBreaches = myMap.values.flatMap { it.toSet() }
                return@flatMap breachRetrieve.getBreachesByIds(neededBreaches)

                    .switchIfEmpty(Maybe.just(emptyList()))
                    .map { result -> myMap to result }
            }

            .map { (myMap, breachesList) ->

                return@map myMap.mapValues { entry ->
                    breachesList.filter { it.id in entry.value }
                }
            }

    }

    override fun getScheduleAllLeaks(scheduleId: ScheduleId): Maybe<List<Breach>> {
        return scanRecordDao.getScheduleAllBreachesIds(scheduleId = scheduleId.toLong())
            .subscribeOn(rxSchedulers.io())
            .onEmptyListComplete()
            .flatMap { breachRetrieve.getBreachesByIds(it) }
    }

    override fun getSessionNewLeaks(sessionId: SessionId?): Maybe<List<Breach>> {
        val source = {
            sessionId?.let { scanRecordDao.getSessionUnNotifiedLeaksIds(it.value) }
                ?: scanRecordDao.getLastSessionUnNotifiedLeaksIds()
        }

        return source()
            .subscribeOn(rxSchedulers.io())
            .onEmptyListComplete()
            .flatMap { breachRetrieve.getBreachesByIds(it) }
    }

    override fun getSessionAllLeaks(sessionId: SessionId): Maybe<List<Breach>> {
        return scanRecordDao.getSessionAllLeaksIds(sessionId = sessionId.value)
            .subscribeOn(rxSchedulers.io())
            .onEmptyListComplete()
            .flatMap { breachRetrieve.getBreachesByIds(it) }
    }

    companion object {
        private const val TAG = "Rob_ScanRecordRepository"
        private const val SQLITE_MAX_VARIABLE_NUMBER = 999
    }
}

