package com.rmsr.myguard.domain.usecase

//import android.util.Log
//import com.google.common.truth.Truth.assertThat
//import com.rmsr.myguard.data.database.entity.ScheduleCheckEntity
//import com.rmsr.myguard.data.database.entity.relations.ScheduleCheckWithBreaches
//import com.rmsr.myguard.data.repository.RelationsRepositoryImpl
//import com.rmsr.myguard.domain.model.ScheduleCheck
//import com.rmsr.myguard.utils.RxJavaSchedulersRule
//import com.rmsr.myguard.utils.MyLog
//import com.rmsr.myguard.utils.logPrint
//import io.mockk.MockKAnnotations
//import io.mockk.clearAllMocks
//import io.mockk.every
//import io.mockk.impl.annotations.MockK
//import io.mockk.mockk
//import io.reactivex.rxjava3.core.Completable
//import io.reactivex.rxjava3.core.Maybe
//import io.reactivex.rxjava3.core.Observable
//import io.reactivex.rxjava3.schedulers.Schedulers
//import io.reactivex.rxjava3.schedulers.TestScheduler
//import org.junit.*
//import org.junit.runner.RunWith
//import org.junit.runners.JUnit4
//import org.mockito.junit.MockitoJUnit
//import org.mockito.junit.MockitoJUnitRunner
//import org.mockito.junit.MockitoRule
//import org.mockito.quality.Strictness
//import java.util.concurrent.TimeUnit


//class SearchCachingFacadeTest {
//
////    @get:Rule(order = 0)
////    val taskExecutorRule = InstantTaskExecutorRule()
//
////    @get:Rule(order = 0)
////    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)
////private  var testScheduler = TestScheduler()
//
//    @get:Rule(order = 1)
//    val rxJavaSchedulersRule = RxJavaSchedulersRule()
//
//
//    companion object {
//        private val scheduleCheckWithBreachesDB = mutableListOf<ScheduleCheckWithBreaches>()
//        private val scheduleCheckDB = mutableListOf<ScheduleCheck>()
//
//        @MockK
//        private lateinit var jobMock: JobRepoImpl
//
//        @MockK
//        private lateinit var relationsMock: RelationsRepositoryImpl
//
//        @BeforeClass
//        @JvmStatic
//        fun init() {
//            MockKAnnotations.init(this)
//
//            every { jobMock.getCheckEmailByQuery(any()) } returns Maybe.empty()
//            every { jobMock.insertEmail(any()) } answers {
//                scheduleCheckDB.add(firstArg<ScheduleCheck>())
//                Completable.complete()
//            }
//
//            every { relationsMock.insertScheduleCheckWithBreaches(any()) } answers {
//                scheduleCheckWithBreachesDB.add(firstArg<ScheduleCheckWithBreaches>())
//                Completable.complete()
//            }
//            every { relationsMock.getScheduleCheckWithBreaches(any()) } answers {
//                Maybe.just(scheduleCheckWithBreachesDB[firstArg<Int>()])
//            }
//        }
//    }
//     fun startUp(){
////        testScheduler.start()
//    }
//    @After
//    fun clearAll() {
//        scheduleCheckDB.clear()
//        scheduleCheckWithBreachesDB.clear()
////        clearAllMocks()
////        testScheduler.shutdown()
//    }
//
//    @Test
//    fun `cache searched query by cacheSearchedQuery _ Successfully insert`() {
//        val cacheFacade = SearchCachingFacade(jobMock, relationsMock)
//
//        val search = "test"
//        val breaches = List(5) {
//            Breach(name = "Test #$it")
//        }
//
//
//        cacheFacade.cacheSearchedQuery(search, breaches)
//            .test()
//            .assertNoErrors()
//            .assertComplete()
//
//        val scheduleWithBreaches = ScheduleCheckWithBreaches(
//            ScheduleCheckEntity(scheduledQuery = search).apply {
//                createdTimeStamp = scheduleCheckWithBreachesDB[0].scheduleCheck.createdTimeStamp
//            },
//            breaches = breaches.map { it.toEntity() }
//        )
//
//        assertThat(scheduleCheckDB.size).isEqualTo(1)
//        assertThat(scheduleCheckDB[0].scheduledQuery).isEqualTo(search)
//
//        assertThat(scheduleCheckWithBreachesDB.size).isEqualTo(1)
//        assertThat(scheduleCheckWithBreachesDB[0]).isEqualTo(scheduleWithBreaches)
//
//    }
//
//    @Test
//    fun `test 2 will deleted`() {
//
//        val cacheFacade = SearchCachingFacade(jobMock, relationsMock)
//
//        val search = "test"
//        val breaches = List(5) {
//            Breach(name = "Test #$it")
//        }
//
//
//        cacheFacade.cacheSearchedQuery(search, breaches)
//            .test()
//            .assertNoErrors()
//            .assertComplete()
//
//        val scheduleWithBreaches = ScheduleCheckWithBreaches(
//            ScheduleCheckEntity(scheduledQuery = search).apply {
//                createdTimeStamp = scheduleCheckWithBreachesDB[0].scheduleCheck.createdTimeStamp
//            },
//            breaches = breaches.map { it.toEntity() }
//        )
//
//        assertThat(scheduleCheckDB.size).isEqualTo(1)
//        assertThat(scheduleCheckDB[0].scheduledQuery).isEqualTo(search)
//
//        assertThat(scheduleCheckWithBreachesDB.size).isEqualTo(1)
//        assertThat(scheduleCheckWithBreachesDB[0]).isEqualTo(scheduleWithBreaches)
//
//    }
//}