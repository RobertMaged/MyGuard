package com.rmsr.myguard.data.database

import com.rmsr.myguard.data.database.dao.BreachDao
import com.rmsr.myguard.data.database.entity.BreachEntity
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.subjects.BehaviorSubject

class FakeBreachDao(initBreaches: List<BreachEntity> = emptyList()) : BreachDao() {
    val db =
        if (initBreaches.any()) initBreaches.toMutableSet() else mutableSetOf()

    private val state = BehaviorSubject.createDefault(db.toList())

//    private val flowable = Flowable.fromObservable(state)

    override fun insert(breaches: List<BreachEntity>): Completable {
        db.addAll(breaches)
        state.onNext(db.toList())
        return Completable.complete()
    }

    override fun deleteById(breachId: Long): Completable {
        db.removeIf { it.id == breachId }
        state.onNext(db.toList())
        return Completable.complete()
    }

    override fun deleteByName(breachName: String): Completable {
        db.removeIf { it.leakName == breachName }
        state.onNext(db.toList())
        return Completable.complete()
    }

    override fun getBreachById(breachId: Long): Maybe<BreachEntity> {
        val breach = db.first { it.id == breachId }
        return Maybe.just(breach)
    }

    override fun getBreachesByIds(breachesIds: List<Long>): Maybe<List<BreachEntity>> {
        val breaches = db.filter { it.id in breachesIds }
        return Maybe.just(breaches)
    }

    override fun getBreachesByNames(breachesNames: List<String>): Maybe<List<BreachEntity>> {
        val breaches = db.filter { it.leakName in breachesNames }
        return Maybe.just(breaches)
    }

    override fun getBreachesIdsByNames(breachesNames: List<String>): Maybe<List<Long>> {
        val breachesIds = db.filter { it.leakName in breachesNames }.map { it.id }
        return Maybe.just(breachesIds)
    }

    override fun searchBreachByDomain(domain: String): Maybe<List<BreachEntity>> {
        val breaches = db.filter { it.domain.equals(domain, ignoreCase = true) }
        return Maybe.just(breaches)
    }

    override fun searchBreachIdByDomain(domain: String): Maybe<List<Long>> {
        val breachesIds =
            db.filter { it.domain.equals(domain, ignoreCase = true) }.map { it.id }
        return Maybe.just(breachesIds)
    }

    override fun searchBreachByDomainName(domainName: String): Maybe<List<BreachEntity>> {
        val breaches = db.filter {
            it.title.startsWith(domainName, ignoreCase = true) || it.domain.startsWith(
                domainName,
                ignoreCase = true
            )
        }
        return Maybe.just(breaches)
    }

    override fun searchBreachIdByDomainName(domainName: String): Maybe<List<Long>> {
        val breachesIds = db.filter {
            it.title.startsWith(domainName, ignoreCase = true) || it.domain.startsWith(
                domainName,
                ignoreCase = true
            )
        }.map { it.id }

        return Maybe.just(breachesIds)
    }

    override fun getAllBreaches(): Maybe<List<BreachEntity>> {
        return Maybe.just(db.toList())
    }

    override fun observeBreaches(): Flowable<List<BreachEntity>> {
        return Flowable.fromObservable(state, BackpressureStrategy.LATEST)
    }
}