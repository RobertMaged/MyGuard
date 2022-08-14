package com.rmsr.myguard.data.database.entity.new_version

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.rmsr.myguard.RxJavaSchedulersRule
import com.rmsr.myguard.data.database.MyGuardDatabase
import com.rmsr.myguard.data.database.dao.HistoryBreachDao
import com.rmsr.myguard.data.database.entity.relations.HistoryBreachXRef
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class HistoryBreachDaoTest {

    @get: Rule(order = 0)
    val hiltAndroidRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    val rxJavaSchedulersRule = RxJavaSchedulersRule()

    @Inject
    lateinit var mMyGuardDatabase: MyGuardDatabase

    private lateinit var historyBreachDao: HistoryBreachDao

    @Before
    fun setUp() {
        hiltAndroidRule.inject()
        historyBreachDao = mMyGuardDatabase.historyBreachDao()
    }

    @After
    fun closeAll() {
        mMyGuardDatabase.close()
    }


    @Test
    fun insertHistoryWithBreachRef_InsertShouldSuccess_returnComplete() {
        val ref = object {
            val historyId = 1L
            val breachId = 1L
        }

        historyBreachDao
            .insertHistoryBreachRef(historyId = ref.historyId, breachId = ref.breachId)
            .test()
            .assertNoErrors()
            .assertComplete()

        val result = mapOf(ref.historyId to listOf(ref.breachId))

        historyBreachDao
            .getHistoriesXRefs(historiesIds = listOf(ref.historyId))
            .test()
            .assertNoErrors()
            .assertValue(result)
    }


    @Test
    fun insertHistoryWithBreachListRef_InsertShouldSuccess_returnComplete() {

        val xrefList = List(6) {

            HistoryBreachXRef(
                historyId = 1,
                breachId = it + 1L
            )
        }

        historyBreachDao
            .insertHistoryBreachRef(xrefList)
            .test()
            .assertNoErrors()
            .assertComplete()

        val result = mapOf(xrefList.first().historyId to xrefList.map { it.breachId })

        historyBreachDao
            .getHistoriesXRefs(historiesIds = listOf(xrefList.first().historyId))
            .test()
            .assertNoErrors()
            .assertValue(result)
    }

    @Test
    fun insertBreachWithHistoryListRef_InsertShouldSuccess_returnComplete() {
        val xrefList = List(6) {

            HistoryBreachXRef(
                breachId = 1,
                historyId = it + 1L
            )
        }

        historyBreachDao
            .insertHistoryBreachRef(xrefList)
            .test()
            .assertNoErrors()
            .assertComplete()

        val result = mapOf(xrefList.first().breachId to xrefList.map { it.historyId })

        historyBreachDao
            .getBreachesXRefs(listOf(xrefList.first().breachId))
            .test()
            .assertNoErrors()
            .assertValue(result)
    }


    @Test
    fun insertBreachListToHistoryListRef_InsertShouldSuccess_returnComplete() {
        val xrefList = List(6) {

            HistoryBreachXRef(
                breachId = (it + 1L) % 3,
                historyId = it + 1L
            )
        }

        historyBreachDao
            .insertHistoryBreachRef(xrefList)
            .test()
            .assertNoErrors()
            .assertComplete()

        val result = xrefList.associateBy(
            keySelector = { before -> before.breachId },
            valueTransform = { before ->
                xrefList.filter { it.breachId == before.breachId }.map { it.historyId }
            }
        )
        // val result = mapOf(xrefList.first().breachId to xrefList.map { it.historyId })

        historyBreachDao
            .getBreachesXRefs(xrefList.map { it.breachId }.toSet().toList())
            .test()
            .assertNoErrors()
            .assertValue(result)
    }


    @Test
    fun insertHistoryListToBreachListRef_InsertShouldSuccess_returnComplete() {
        val xrefList = List(6) {

            HistoryBreachXRef(
                historyId = (it + 1L) % 3,
                breachId = it + 1L
            )
        }

        historyBreachDao
            .insertHistoryBreachRef(xrefList)
            .test()
            .assertNoErrors()
            .assertComplete()

        val result = xrefList.associateBy(
            keySelector = { before -> before.historyId },
            valueTransform = { before ->
                xrefList.filter { it.historyId == before.historyId }.map { it.breachId }
            }
        )
        // val result = mapOf(xrefList.first().breachId to xrefList.map { it.historyId })

        historyBreachDao
            .getHistoriesXRefs(xrefList.map { it.historyId }.toSet().toList())
            .test()
            .assertNoErrors()
            .assertValue(result)
    }

    @Test
    fun insertSameHistoryWithBreachRefManyTimes_InsertConflictShouldIgnored_returnComplete() {
        val xrefList = List(6) {
            HistoryBreachXRef(
                historyId = (it + 1L) % 3,
                breachId = it + 1L % 3
            )
        }

        repeat(6) {

            historyBreachDao
                .insertHistoryBreachRef(historyBreachList = xrefList)
                .test()
                .assertNoErrors()
                .assertComplete()
        }


        historyBreachDao
            .getAll()
            .test()
            .assertNoErrors()
            .assertValue(xrefList.take(3))


    }
}