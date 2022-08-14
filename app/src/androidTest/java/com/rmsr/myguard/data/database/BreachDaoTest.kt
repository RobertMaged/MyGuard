package com.rmsr.myguard.data.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.rmsr.myguard.data.database.dao.BreachDao
import com.rmsr.myguard.data.database.entity.BreachEntity
import com.rmsr.myguard.utils.onEmptyListComplete
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject


@SmallTest
@HiltAndroidTest
class BreachDaoTest() {

    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @get:Rule
    var instanceTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var mMyGuardDatabase: MyGuardDatabase
    private lateinit var mBreachDao: BreachDao

    @Before
    fun setUp() {
        hiltAndroidRule.inject()
        mBreachDao = mMyGuardDatabase.breachDao()
    }

    @After
    fun tearDown() {
        mMyGuardDatabase.close()
    }


    @Test
    fun insertBreachItem() {
        val breachEntities = List(5) {
            BreachEntity(
                leakName = "Test #$it",
                createdTime = 0,
                logoPath = "no logo",
                title = "",
                description = "",
                domain = "",
                date = "",
                pwnCount = 0,
                compromisedData = emptyList()
            )
        }

        val insertObserver = mBreachDao.insert(breachEntities).test()
        insertObserver.assertComplete()

        val checkObserver = mBreachDao.getAllBreaches().test()

        val result = breachEntities.mapIndexed { i, breach -> breach.copy(id = i + 1L) }

        checkObserver.assertValue(result)
    }

    @Test
    fun insertBreachItems() {
        val breachEntities = List(5) {
            BreachEntity(
                leakName = "Test #$it",
                createdTime = 0,
                logoPath = "no logo",
                title = "",
                description = "",
                domain = "",
                date = "",
                pwnCount = 0,
                compromisedData = emptyList()
            )
        }

        val insertObserver = mBreachDao.insert(breaches = breachEntities).test()
        val checkObserver = mBreachDao.getAllBreaches().test()

        val result = breachEntities.mapIndexed { i, breach -> breach.copy(id = i + 1L) }
        insertObserver
            .assertNoErrors()
        //.assertValue(result.map { it.id })


        checkObserver.assertValue(result)
    }

    @Test
    fun getBreachByName() {
        val breachEntities = List(4) {
            BreachEntity(
                leakName = "Test #$it",
                createdTime = 0,
                logoPath = "no logo",
                title = "",
                description = "",
                domain = "",
                date = "",
                pwnCount = 0,
                compromisedData = emptyList()
            )
        }

        val insertObserver = mBreachDao.insert(breachEntities).test()
        insertObserver.assertComplete()

        val checkObserver = mBreachDao.getBreachesByNames(listOf(breachEntities[1].leakName))
            .onEmptyListComplete()
            .map { it.first() }
            .test()

        val result = breachEntities.mapIndexed { i, breach -> breach.copy(id = i + 1L) }
        checkObserver.assertNoErrors()
        checkObserver.assertValue(result[1])
    }

    @Test
    fun getBreachesListByNames() {
        val breachEntities = List(12) {
            BreachEntity(
                leakName = "Test #$it",
                createdTime = 0,
                logoPath = "no logo",
                title = "",
                description = "",
                domain = "",
                date = "",
                pwnCount = 0,
                compromisedData = emptyList()
            )
        }

        val insertObserver = mBreachDao.insert(breachEntities).test()
        insertObserver.assertComplete()

        val breachesNames = breachEntities.subList(5, 9).map { it.leakName }
        val checkObserver = mBreachDao.getBreachesByNames(breachesNames).toObservable()
            .take(1)
            .test()

        val result = breachEntities.mapIndexed { i, breach -> breach.copy(id = i + 1L) }
        checkObserver.assertNoErrors()
        checkObserver.assertComplete()

        checkObserver.assertResult(result.subList(5, 9))
    }

    @Test
    fun getBreachesByDomain() {
        val breachEntities = List(4) {
            BreachEntity(
                leakName = "Test #$it",
                createdTime = 0,
                logoPath = "no logo",
                title = "",
                description = "",
                domain = "domain #$it",
                date = "",
                pwnCount = 0,
                compromisedData = emptyList()
            )
        }

        val insertObserver = mBreachDao.insert(breachEntities).test()
        insertObserver.assertComplete()

        val checkObserver = mBreachDao.searchBreachByDomain(breachEntities[3].domain).test()

        val result = breachEntities.mapIndexed { i, breach -> breach.copy(id = i + 1L) }
        checkObserver.assertNoErrors()
        checkObserver.assertValue(result.takeLast(1))
    }

    @Test
    fun deleteBreachByName() {
        val breachEntities = List(6) {
            BreachEntity(
                leakName = "Test #$it",
                createdTime = 0,
                logoPath = "no logo",
                title = "",
                description = "",
                domain = "",
                date = "",
                pwnCount = 0,
                compromisedData = emptyList()
            )
        }

        val insertObserver = mBreachDao.insert(breachEntities).test()
        insertObserver.assertComplete()

        val checkObserver = mBreachDao.deleteByName(breachName = "Test #5").test()
        checkObserver.assertNoErrors()
        checkObserver.assertComplete()

        val result = breachEntities.mapIndexed { i, breach -> breach.copy(id = i + 1L) }
        val afterDeleteObserver = mBreachDao.getAllBreaches().test()
        afterDeleteObserver.assertComplete()
            .assertValue(result.subList(0, 5))

        assertThat(afterDeleteObserver.values().first().size).isEqualTo(5)
    }

    @Test
    fun deleteBreachById() {
        val breachEntities = List(6) {
            BreachEntity(
                leakName = "Test #$it",
                createdTime = 0,
                logoPath = "no logo",
                title = "",
                description = "",
                domain = "",
                date = "",
                pwnCount = 0,
                compromisedData = emptyList()
            )
        }

        val insertObserver = mBreachDao.insert(breachEntities).test()
        insertObserver.assertComplete()

        val checkObserver = mBreachDao.deleteById(breachId = 6L).test()
        checkObserver.assertNoErrors()
        checkObserver.assertComplete()

        val result = breachEntities.mapIndexed { i, breach -> breach.copy(id = i + 1L) }
        val afterDeleteObserver = mBreachDao.getAllBreaches().test()
        afterDeleteObserver.assertComplete()
            .assertValue(result.subList(0, 5))

        assertThat(afterDeleteObserver.values().first().size).isEqualTo(5)
    }

    @Test
    fun insertBreachIgnoredOnNamesConflict() {
        val breachEntities = List(4) {
            BreachEntity(
                leakName = "Test #${it + 1}",
                createdTime = (it + 1) * 100L,
                logoPath = "no logo",
                title = "",
                description = "",
                domain = "",
                date = "",
                pwnCount = 0,
                compromisedData = emptyList()
            )
        }

        var insertObserver = mBreachDao.insert(breachEntities).test()
        insertObserver.assertComplete()

        val doubledBreaches = breachEntities.map { breach ->
            val newTime = breach.createdTime * 15
            breach.copy(createdTime = newTime)
        }

        insertObserver = mBreachDao.insert(doubledBreaches).test()
        insertObserver.assertComplete()

        val afterSecondInsertObserver = mBreachDao.getAllBreaches().test()

        val result = breachEntities.mapIndexed { i, breach -> breach.copy(id = i + 1L) }
        afterSecondInsertObserver.assertNoErrors()
        afterSecondInsertObserver.assertValue(result)
    }
}