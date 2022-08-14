package com.rmsr.myguard.data.database.dao

import androidx.room.*
import com.rmsr.myguard.data.database.entity.QueryEntity
import com.rmsr.myguard.data.database.entity.ScheduleEntity
import com.rmsr.myguard.data.database.entity.SearchHistoryEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import java.util.*


@Dao
abstract class QueryDao {

    private val query = QueryEntity.SCHEMA

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insert(queryEntity: QueryEntity): Single<Long>

    /**
     * Since [uuid] column is unique in [QueryEntity],
     * this method check first if QueryEntity is exist based on uuid and return its id,
     * if not new one inserted.
     *
     * Prefer this method over normal [insert] method if you don't need
     * to check first if [QueryEntity] exists or not and if you don't need handle [SQLITE_CONSTRAINT_UNIQUE] yourself.
     * @return id of [QueryEntity] in both cases (inserted or get).
     */
    //@Transaction
    fun insertOrGet(queryEntity: QueryEntity): Single<Long> {
        return getQueryId(queryUUID = queryEntity.uuid)
            .switchIfEmpty(insert(queryEntity = queryEntity))
    }

    /**
     * This method make sure that only mutable fields will be updated, otherwise new row will be inserted.
     *
     * [QueryEntity] is a parent table for [ScheduleEntity] and [SearchHistoryEntity] tables,
     * so not all fields are mutable due to these relations.
     * @return Id of QueryEntity after updates take place.
     */
    fun updateAndGetId(queryEntity: QueryEntity): Single<Long> {
        //uuid generated based on query and queryType, so that means if it is exists we just update the other fields.
        return getQueryId(queryEntity.uuid)
            //this means it exists, so just update it.
            .flatMapSingle { queryId -> update(queryEntity).toSingleDefault(queryId) }
            //if not exist just insert new one.
            .switchIfEmpty(insert(queryEntity = queryEntity))
    }

    @Update
    protected abstract fun update(queryEntity: QueryEntity): Completable

    @Suppress("not used.")
    @Query("UPDATE ${query.TABLE_NAME} SET ${query.HINT} =:hint WHERE ${query.ID} =:queryId")
    abstract fun updateQueryHint(queryId: Long, hint: String?): Completable

    @Query("SELECT ${query.ID} FROM ${query.TABLE_NAME} WHERE ${query.UUID} = :queryUUID")
    abstract fun getQueryId(queryUUID: UUID): Maybe<Long>

    @Query("SELECT * FROM ${query.TABLE_NAME} WHERE ${query.UUID} = :queryUUID")
    abstract fun getQuery(queryUUID: UUID): Maybe<QueryEntity>

    @Query("SELECT * FROM ${query.TABLE_NAME} WHERE ${query.ID} = :queryId")
    abstract fun getQuery(queryId: Long): Maybe<QueryEntity>
}