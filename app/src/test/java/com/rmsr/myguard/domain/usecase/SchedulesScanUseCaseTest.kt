package com.rmsr.myguard.domain.usecase

import com.rmsr.myguard.data.repository.BreachRepositoryImpl
import com.rmsr.myguard.data.repository.SessionRepositoryImpl
import com.rmsr.myguard.domain.entity.BreachId
import com.rmsr.myguard.domain.entity.Schedule
import com.rmsr.myguard.domain.entity.SearchQuery
import com.rmsr.myguard.domain.entity.SessionId
import com.rmsr.myguard.domain.repository.BreachRepository
import com.rmsr.myguard.domain.repository.ScanRecordsRepository
import com.rmsr.myguard.domain.repository.ScheduleRetrieveRepo
import com.rmsr.myguard.domain.usecase.schedules.SchedulesScanUseCase
import com.rmsr.myguard.utils.RxJavaSchedulersRule
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.*
import java.lang.reflect.Method
import java.util.concurrent.TimeUnit

class SchedulesScanUseCaseTest {

    private val testScheduler = TestScheduler()

    @get:Rule
    var rxRule = RxJavaSchedulersRule(testScheduler)

    companion object {
        private val dummySessionId = SessionId(2L)
        private val schedulesCount = 10

        @MockK
        private lateinit var sessionRepo: SessionRepositoryImpl

        @MockK
        private lateinit var schedulesRetrieve: ScheduleRetrieveRepo

        @MockK
        private lateinit var scanRecordsRepository: ScanRecordsRepository

        @MockK
        private lateinit var breachRepository: BreachRepository

        @JvmStatic
        @BeforeClass
        fun init() {
            MockKAnnotations.init(this)

            every { sessionRepo.initNewScanSession() } returns Single.just(dummySessionId)
            every { sessionRepo.updateSession(any()) } returns Completable.complete()
            every {
                sessionRepo.saveSessionScannedSchedules(any(), any())
            } returns Completable.complete()
            every { scanRecordsRepository.createScanRecords(any()) } returns Completable.complete()
        }
    }

    private lateinit var periodicScan: SchedulesScanUseCase

    private fun dummySchedulesSource(): Maybe<List<Schedule>> {
        val schedules = List(schedulesCount) {
            SearchQuery.Email("test_email$it@yahoo.com")
            Schedule(id = it.toLong(), searchQuery = SearchQuery.Email("test_email$it@yahoo.com"))
        }
        return Maybe.just(schedules)
    }

    private fun dummySearchSource(): Maybe<List<BreachId>> {
        val breachesIds = List(100) { BreachId(it.toLong()) }
        return Maybe.just(breachesIds)
    }

    @Before
    fun setUp() {
        periodicScan = SchedulesScanUseCase(
            sessionRepository = sessionRepo,
            scheduleRetrieve = schedulesRetrieve,
            breachRepository = breachRepository,
            scanRecordsRepository = scanRecordsRepository,
//            errorHandler = ErrorHandleImpl()
        )
        testScheduler.start()
    }

    @After
    fun tearDown() {
        clearMocks(
            sessionRepo,
            recordedCalls = true,
            exclusionRules = true,
            answers = false,
            childMocks = false,
            verificationMarks = false,
        )
        clearMocks(
            schedulesRetrieve, mocks = arrayOf(breachRepository),
            recordedCalls = true,
            exclusionRules = true,
            answers = false,
            childMocks = true,
            verificationMarks = true,
        )
//        clearAllMocks(
//            recordedCalls = true,
//            answers = false,
//            childMocks = false,
//            regularMocks = false,
//            objectMocks = false,
//            staticMocks = false,
//            constructorMocks = false)
        testScheduler.shutdown()
    }

    @Test
    fun `give schedules and fake breaches result when scan session done return session id`() {
        every { schedulesRetrieve.getAllSchedules() } returns dummySchedulesSource()
        every { breachRepository.searchForLeaksIds(any()) } returns dummySearchSource()

        val observer = periodicScan.invoke(
//            scheduleSource = this::dummySchedulesSource,
//            searchSource = this::dummySearchSource
        )
            .test()

        testScheduler.advanceTimeBy(
            BreachRepositoryImpl.API_BREACHES_RATE_LIMIT_MILLIS.toLong() * (schedulesCount),
            TimeUnit.MILLISECONDS
        )

        verifyOrder {
            sessionRepo.initNewScanSession()
            sessionRepo.saveSessionScannedSchedules(any(), any())
            sessionRepo.updateSession(any())
        }

        observer.assertNoErrors()
            .assertValue((dummySessionId))
    }

    @Test
    fun `give schedules and with no leaks found result when scan session done return session id`() {
        // ignore breaches ids coming from dummySearchSource().
        val noResultSource: (SearchQuery) -> Maybe<List<BreachId>> =
            { dummySearchSource().flatMap { Maybe.empty() } }

        every { schedulesRetrieve.getAllSchedules() } returns dummySchedulesSource()
        every { breachRepository.searchForLeaksIds(any()) } returns Maybe.empty()

        val observer = periodicScan.invoke(
//            scheduleSource = this::dummySchedulesSource,
//            searchSource = noResultSource
        )
            .test()

        testScheduler.advanceTimeBy(
            BreachRepositoryImpl.API_BREACHES_RATE_LIMIT_MILLIS.toLong() * schedulesCount,
            TimeUnit.MILLISECONDS
        )

        verifyOrder {
            sessionRepo.initNewScanSession()
            sessionRepo.saveSessionScannedSchedules(any(), any())
            sessionRepo.updateSession(any())
        }

        observer.assertNoErrors()
            .assertValue(dummySessionId)

    }

    @Test
    fun `give empty schedules and fake breaches result when scan session done return nothing and complete`() {
        // ignore schedules coming from dummySchedulesSource().
        val emptySchedulesSource: () -> Maybe<List<Schedule>> =
            { dummySchedulesSource().flatMap { Maybe.empty() } }

        fun emptySchedulesSource2(): Maybe<List<Schedule>> {

            return Maybe.empty<List<Schedule>>()
        }
        every { schedulesRetrieve.getAllSchedules() } returns Maybe.empty()
        every { breachRepository.searchForLeaksIds(any(), any()) } returns dummySearchSource()

        periodicScan.invoke(
//            scheduleSource = ::emptySchedulesSource2,
//            searchSource = this::dummySearchSource
        )
            .test()
            .assertNoErrors()
            .assertNoValues()
            .assertComplete()

        // make sure this methods not called.
        verify(exactly = 0) {
            sessionRepo.initNewScanSession()
            sessionRepo.saveSessionScannedSchedules(any(), any())
            sessionRepo.updateSession(any())
        }
    }


    @Test
    fun `check initSession method`() {
        val initSession: Method = periodicScan.javaClass.getDeclaredMethod("initSession")
            .apply { isAccessible = true }

        val sessionId = initSession.invoke(periodicScan) as Single<SessionId>

        sessionId.test()
            .assertValue(dummySessionId)

    }
}