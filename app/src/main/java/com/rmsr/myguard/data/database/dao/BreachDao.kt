package com.rmsr.myguard.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rmsr.myguard.data.database.entity.BreachEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe


@Dao
abstract class BreachDao {
    private val breach = BreachEntity.SCHEMA

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(breaches: List<BreachEntity>): Completable

    @Query("DELETE from ${breach.TABLE_NAME} WHERE ${breach.ID} =:breachId")
    abstract fun deleteById(breachId: Long): Completable

    @Query("DELETE from ${breach.TABLE_NAME} WHERE ${breach.LEAK_NAME} =:breachName")
    abstract fun deleteByName(breachName: String): Completable

    ////////////////////////////////

    @Query("SELECT * FROM ${breach.TABLE_NAME} WHERE ${breach.ID} =:breachId")
    abstract fun getBreachById(breachId: Long): Maybe<BreachEntity>

    @Query("SELECT * FROM ${breach.TABLE_NAME} WHERE ${breach.ID} IN (:breachesIds)")
    abstract fun getBreachesByIds(breachesIds: List<Long>): Maybe<List<BreachEntity>>

    @Query("SELECT * FROM ${breach.TABLE_NAME} WHERE ${breach.LEAK_NAME} IN (:breachesNames)")
    abstract fun getBreachesByNames(breachesNames: List<String>): Maybe<List<BreachEntity>>

    @Query("SELECT ${breach.ID} FROM ${breach.TABLE_NAME} WHERE ${breach.LEAK_NAME} IN (:breachesNames)")
    abstract fun getBreachesIdsByNames(breachesNames: List<String>): Maybe<List<Long>>

    @Query("SELECT * FROM ${breach.TABLE_NAME} WHERE ${breach.DOMAIN} =:domain")
    abstract fun searchBreachByDomain(domain: String): Maybe<List<BreachEntity>>

    @Query("SELECT ${breach.TABLE_NAME}.${breach.ID} FROM ${breach.TABLE_NAME} WHERE ${breach.DOMAIN} =:domain")
    abstract fun searchBreachIdByDomain(domain: String): Maybe<List<Long>>

    @Query("SELECT * FROM ${breach.TABLE_NAME} WHERE ${breach.TITLE} LIKE :domainName || '%' OR ${breach.DOMAIN} LIKE :domainName || '%'")
    abstract fun searchBreachByDomainName(domainName: String): Maybe<List<BreachEntity>>

    @Query("SELECT ${breach.TABLE_NAME}.${breach.ID} FROM ${breach.TABLE_NAME} WHERE ${breach.TITLE} LIKE '%' || :domainName || '%' OR ${breach.DOMAIN} LIKE '%' || :domainName || '%'")
    abstract fun searchBreachIdByDomainName(domainName: String): Maybe<List<Long>>

    @Query("SELECT * from ${breach.TABLE_NAME}")
    abstract fun getAllBreaches(): Maybe<List<BreachEntity>>

    @Suppress("Unused")
    @Query("SELECT * from ${breach.TABLE_NAME}")
    abstract fun observeBreaches(): Flowable<List<BreachEntity>>


}