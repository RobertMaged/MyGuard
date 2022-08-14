package com.rmsr.myguard.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.rmsr.myguard.data.database.dao.QueryDao
import com.rmsr.myguard.data.database.dao.ScheduleDao
import com.rmsr.myguard.data.database.entity.QueryEntity
import com.rmsr.myguard.data.database.entity.ScheduleEntity
import com.rmsr.myguard.data.database.entity.relations.ScheduleWithQuery
import com.rmsr.myguard.data.mapper.ReversibleMapper
import com.rmsr.myguard.domain.entity.Schedule
import com.rmsr.myguard.domain.entity.ScheduleId
import com.rmsr.myguard.domain.entity.SearchQuery
import com.rmsr.myguard.domain.entity.errors.AlreadyExistedRecordError
import com.rmsr.myguard.domain.repository.ScheduleRetrieveRepo
import com.rmsr.myguard.domain.repository.ScheduleWriteRepo
import com.rmsr.myguard.utils.RxSchedulers
import com.rmsr.myguard.utils.component1
import com.rmsr.myguard.utils.component2
import com.rmsr.myguard.utils.onEmptyListComplete
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleRepositoryImpl @Inject constructor(
    private val queryDao: QueryDao,
    private val scheduleDao: ScheduleDao,
    private val scheduleMapper: ReversibleMapper<ScheduleWithQuery, Schedule>,
    private val queryMapper: ReversibleMapper<QueryEntity, SearchQuery>,
    private val rxSchedulers: RxSchedulers,
) : ScheduleWriteRepo, ScheduleRetrieveRepo {

    //TODO copy xref from history breach.
    override fun createNewSchedule(query: SearchQuery): Completable {
        val queryEntity = queryMapper.mapReversed(query)

        return queryDao.insertOrGet(queryEntity = queryEntity)
            .subscribeOn(rxSchedulers.io())

            .timestamp()
            .map { (time, queryId) ->
                ScheduleEntity(queryId = queryId, createdTime = time)
            }

            .flatMap { entity -> scheduleDao.insert(entity) }
            //UNIQUE constraint failed: schedules.query_id (Sqlite code 2067), (OS error - 11:Try again)
            .onErrorResumeNext { error ->
                if (error is SQLiteConstraintException)
                    Single.error(
                        AlreadyExistedRecordError(
                            error.message ?: "Schedule already existed.",
                            error
                        )
                    )
                else
                    Single.error(error)
            }

            // inserted schedule id is ignored.
            .flatMapCompletable { Completable.complete() }
    }

    override fun isSearchQueryScheduled(query: SearchQuery): Single<Boolean> {
        val queryEntity = queryMapper.mapReversed(query)

        return queryDao.getQueryId(queryUUID = queryEntity.uuid)
            .subscribeOn(rxSchedulers.io())

            .flatMap { queryId -> scheduleDao.findQueryScheduleId(queryId) }
            .map { true }

            .defaultIfEmpty(false)
    }

    // FIXME: issue here.
    override fun editSchedule(schedule: Schedule): Completable {
        return TODO("There is issue here.")
        val scheduleWithQuery = scheduleMapper.mapReversed(schedule)

        return queryDao.updateAndGetId(queryEntity = scheduleWithQuery.queryEntity)
            .subscribeOn(rxSchedulers.io())
            //update schedule entity with new query reference and other fields.
            .map { newQueryId ->
                scheduleWithQuery.scheduleEntity.copy(queryId = newQueryId)
            }
            //update scheduleEntity
            .flatMapCompletable { newScheduleEntity -> scheduleDao.update(scheduleEntity = newScheduleEntity) }
    }

    override fun deleteSchedule(scheduleId: ScheduleId): Completable {
        return scheduleDao.delete(scheduleId = scheduleId.value)
            .subscribeOn(rxSchedulers.io())
    }

    override fun getAllSchedules(): Maybe<List<Schedule>> {
        return scheduleDao.observeSchedulesWithQueries()
            .subscribeOn(rxSchedulers.io())
            .firstElement()
            .onEmptyListComplete()
            .map {
                scheduleMapper.map(it)
            }
    }

    override fun getSchedule(scheduleId: ScheduleId): Maybe<Schedule> {
        return scheduleDao.getScheduleWithQuery(scheduleId = scheduleId.value)
            .subscribeOn(rxSchedulers.io())
            .map {
                scheduleMapper.map(it)
            }
    }

    override fun getSchedules(schedulesIds: Set<ScheduleId>): Maybe<List<Schedule>> {
        val ids: List<Long> = schedulesIds.map { it.value }

        return scheduleDao.getSchedulesWithQueries(ids)
            .subscribeOn(rxSchedulers.io())
            .map {
                scheduleMapper.map(it)
            }
    }

    private val sharedSchedulesSrc = schedulesSrc().share()
    override fun observeSchedules(): Flowable<List<Schedule>> = sharedSchedulesSrc

    private fun schedulesSrc(): Flowable<List<Schedule>> {

        return scheduleDao.observeSchedulesWithQueries()
            .subscribeOn(rxSchedulers.io())
            .onBackpressureLatest()
            .distinctUntilChanged()
            .map {
                scheduleMapper.map(it)
            }
    }
}