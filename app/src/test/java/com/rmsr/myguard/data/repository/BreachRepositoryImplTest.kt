package com.rmsr.myguard.data.repository

import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rmsr.myguard.data.database.FakeBreachDao
import com.rmsr.myguard.data.database.history_caching.HistoryCachingStrategyFactory
import com.rmsr.myguard.data.database.history_caching.HistoryDisabledStrategy
import com.rmsr.myguard.data.mapper.BreachEntityMapper
import com.rmsr.myguard.data.mapper.BreachRepositoryMappers
import com.rmsr.myguard.data.mapper.BreachResponseMapper
import com.rmsr.myguard.data.mapper.QueryEntityMapper
import com.rmsr.myguard.data.remote.BreachRemoteDataSourceInitFacade
import com.rmsr.myguard.data.remote.BreachesRemoteDataSource
import com.rmsr.myguard.data.remote.pojo_response.BreachResponse
import com.rmsr.myguard.domain.entity.Breach
import com.rmsr.myguard.domain.entity.SearchQuery
import com.rmsr.myguard.domain.entity.errors.ErrorEntity
import com.rmsr.myguard.utils.RxJavaSchedulersRule
import com.rmsr.myguard.utils.RxTestSchedulers
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.observers.TestObserver
import io.reactivex.rxjava3.schedulers.TestScheduler
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class BreachRepositoryImplTest {

    @get:Rule
    var rxRule = RxJavaSchedulersRule(testScheduler)

    companion object {
        private val testScheduler = TestScheduler()
        private const val RATE_LIMIT = BreachRepositoryImpl.API_BREACHES_RATE_LIMIT_MILLIS.toLong()

        private val rxSchedulers = RxTestSchedulers(testScheduler)


        private val breachMappers = BreachRepositoryMappers(
            BreachResponseMapper(), BreachEntityMapper(), QueryEntityMapper()
        )

        private lateinit var breachDao: FakeBreachDao

        private lateinit var breachRemoteSource: BreachesRemoteDataSource

        @MockK
        private lateinit var apiFacade: BreachRemoteDataSourceInitFacade

        @MockK
        private lateinit var historyStrategyFactory: HistoryCachingStrategyFactory

        private lateinit var server: MockWebServer

        private val allBreachesJson: String = getJson("all_breaches")

        @JvmStatic
        @BeforeClass
        fun init() {
            MockKAnnotations.init(this)

            breachDao = FakeBreachDao().apply {
                val allBreaches = Gson().fromJson<List<BreachResponse>>(
                    allBreachesJson,
                    object : TypeToken<List<BreachResponse>>() {}.type
                )

                val breachesEntities = breachMappers.breachResponse.map(allBreaches)
                insert(breachesEntities)
            }

            /*server = MockWebServer().apply {

                start()
                val baseUrl = url("/")

                breachRemoteSource = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(
                        RxJava3CallAdapterFactory.createSynchronous()
                    )
                    .build()
                    .create(BreachesRemoteDataSource::class.java)

            }*/

            every { apiFacade.initIfNot() } returns Completable.complete()
            every { apiFacade.api } answers { breachRemoteSource }

            every { historyStrategyFactory.fromMode(any()) } returns HistoryDisabledStrategy()
        }

    }

    private lateinit var breachRepository: BreachRepositoryImpl

    @Before
    fun startUp() {
        server = MockWebServer()
        testScheduler.start()
        server.start()
        val baseUrl = server.url("/")

        breachRemoteSource = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(
                RxJava3CallAdapterFactory.createSynchronous()
            )
            .build()
            .create(BreachesRemoteDataSource::class.java)


        breachRepository = BreachRepositoryImpl(
            apiFacade, breachDao, breachMappers,
            historyStrategyFactory, rxSchedulers
        )
    }

    @After
    fun tearDown() {
        server.shutdown()
        testScheduler.shutdown()
    }

    @Test
    fun `given search query to searchForLeaks return two breaches`() {
        val twoBreachResponse = MockResponse().apply {
            setResponseCode(200)
            setBody("""[{"Name":"Adobe"},{"Name":"Gawker"}]""")
        }
        server.enqueue(twoBreachResponse)

        val observer = breachRepository.searchForLeaks(SearchQuery.Email("test")).test()

        testScheduler.triggerActions()
        observer.assertNoErrors()
            .assertValue { it.size == 2 }

        val request = server.takeRequest(1000, TimeUnit.MILLISECONDS)


        assertThat(
            request?.path?.startsWith(
                BreachesRemoteDataSource.BREACH_ACCOUNT_SERVICE,
                true
            )
        ).isTrue()

    }


    ///////////////////////// Rate limit test ////////////////////////////////////////

    @Test
    fun `make sure call searchForLeaks() respects rateLimit`() {
        val twoBreachResponse = MockResponse().apply {
            setResponseCode(200)
            setBody("""[{"Name":"Adobe"},{"Name":"Gawker"}]""")
        }
        val noLeaksErrorResponse = MockResponse().apply {
            setResponseCode(404)
        }
        val serviceNotAvailableErrorResponse = MockResponse().apply {
            setResponseCode(503)
        }
        server.enqueue(twoBreachResponse)
        server.enqueue(noLeaksErrorResponse)
        server.enqueue(twoBreachResponse)
        server.enqueue(serviceNotAvailableErrorResponse)

        val queries = List(4) { SearchQuery.Email("test") }
        val observers = mutableListOf<TestObserver<List<Breach>>>()

        for ((i, query) in queries.withIndex())
            observers.add(i, breachRepository.searchForLeaks(query).test())


        testScheduler.triggerActions()
        observers[0].assertNoErrors().assertValue { it.size == 2 }
        observers[1].assertNotComplete().assertNoErrors().assertNoValues()
        observers[2].assertNotComplete().assertNoErrors().assertNoValues()
        observers[3].assertNotComplete().assertNoErrors().assertNoValues()

        testScheduler.advanceTimeBy(
            RATE_LIMIT + 2,
            TimeUnit.MILLISECONDS
        )
        observers[1].assertNoErrors().assertNoValues().assertComplete()
        observers[2].assertNotComplete().assertNoErrors().assertNoValues()
        observers[3].assertNotComplete().assertNoErrors().assertNoValues()

        testScheduler.advanceTimeBy(
            RATE_LIMIT + 2,
            TimeUnit.MILLISECONDS
        )
        observers[2].assertNoErrors().assertValue { it.size == 2 }
        observers[3].assertNotComplete().assertNoErrors().assertNoValues()

        testScheduler.advanceTimeBy(
            RATE_LIMIT + 2,
            TimeUnit.MILLISECONDS
        )
        observers[3].assertNotComplete().assertNoValues().assertError {
            it is ErrorEntity.ApiError && it.responseCode == 503
        }
    }


    @Ignore("Must Run on non test threads.")
    @Test
    fun `make sure call searchForLeaks() for List respects rateLimit`() {
        val twoBreachResponse = MockResponse().apply {
            setResponseCode(200)
            setBody("""[{"Name":"Adobe"},{"Name":"Gawker"}]""")
        }

        repeat(4) { server.enqueue(twoBreachResponse) }

        val queries = List(4) { SearchQuery.Email("test") }

        val groupObserver = Observable.fromIterable(queries)
            .flatMapMaybe { query ->
                breachRepository.searchForLeaksIds(query)//.subscribeOn(Schedulers.io())
            }
            .test()

        Observable.interval(400, RATE_LIMIT + 2, TimeUnit.MILLISECONDS)
            .take(4)
            .map {
                when (it.toInt()) {
                    0 -> groupObserver.assertNoErrors().assertNotComplete().assertValueCount(1)
                    1 -> groupObserver.assertNoErrors().assertNotComplete().assertValueCount(2)
                    2 -> groupObserver.assertNoErrors().assertNotComplete().assertValueCount(3)
                    4 -> groupObserver.assertNoErrors().assertValueCount(4).assertComplete()
                    else -> Unit
                }
            }
            .blockingSubscribe()

    }

    @Test
    fun `make sure all requests queue disposed when one request return rateLimit error`() {
        val twoBreachResponse = MockResponse().apply {
            setResponseCode(200)
            setBody("""[{"Name":"Adobe"},{"Name":"Gawker"}]""")
        }
        val errorResponse = MockResponse().apply {
            setResponseCode(429)
            addHeader("retry-after", "4")
            setBody("""{ "statusCode": 429, "message": "Rate limit is exceeded. Try again in 4 seconds." }""")
        }
        server.enqueue(twoBreachResponse)
        server.enqueue(errorResponse)
        server.enqueue(twoBreachResponse)

        val queries = List(3) { SearchQuery.Email("test") }
        val observers = mutableListOf<TestObserver<List<Breach>>>()

        for ((i, query) in queries.withIndex())
            observers.add(i, breachRepository.searchForLeaks(query).test())


        testScheduler.triggerActions()
        observers[0].assertNoErrors().assertValue { it.size == 2 }
        observers[1].assertNotComplete().assertNoErrors().assertNoValues()
        observers[2].assertNotComplete().assertNoErrors().assertNoValues()

        testScheduler.advanceTimeBy(
            RATE_LIMIT + 2,
            TimeUnit.MILLISECONDS
        )
        observers[1].assertError {
            it is ErrorEntity.ApiError && it.responseCode == 429
        }
        observers[2].assertNotComplete().assertNoErrors().assertNoValues()

        testScheduler.advanceTimeBy(
            RATE_LIMIT + 1000,
            TimeUnit.MILLISECONDS
        )

        observers[2].assertNotComplete().assertNoErrors().assertNoValues()
    }

    @Test
    fun `api return rateLimit error, watch waitMillis updates`() {
        val twoBreachResponse = MockResponse()
        twoBreachResponse.setResponseCode(200)
        twoBreachResponse.setBody("""[{"Name":"Adobe"},{"Name":"Gawker"}]""")

        val tryAfterSeconds = 4

        val errorResponse = MockResponse()
        errorResponse.setResponseCode(429)
        errorResponse.addHeader("retry-after", tryAfterSeconds.toString())
        errorResponse.setBody("""{ "statusCode": 429, "message": "Rate limit is exceeded. Try again in $tryAfterSeconds seconds." }""")

        repeat(4) { i ->
            server.enqueue(if (i == 1) errorResponse else twoBreachResponse)
        }


//        val waitMillis = breachRepository.javaClass.getDeclaredField("waitMillis")
        val waitMillis = breachRepository.javaClass.getDeclaredField("rateLimitManager")
            .let { reflectField ->
                reflectField.isAccessible = true

                val innerClass = reflectField[breachRepository]
                innerClass.javaClass.getDeclaredField("waitMillis").let {
                    it.isAccessible = true
                    it[innerClass] as AtomicInteger
                }
            }

        assertThat(waitMillis.get()).isEqualTo(RATE_LIMIT)

        val queries = List(3) { SearchQuery.Email("test") }
        val observers = mutableListOf<TestObserver<List<Breach>>>()

        for ((i, query) in queries.withIndex())
            observers.add(i, breachRepository.searchForLeaks(query).test())


        testScheduler.triggerActions()

        assertThat(waitMillis.get()).isEqualTo(RATE_LIMIT)

        testScheduler.advanceTimeBy(
            RATE_LIMIT + 2,
            TimeUnit.MILLISECONDS
        )
        observers[1].assertError {
            assertThat(it.message).contains("busy")
            it is ErrorEntity.ApiError && it.responseCode == 429
        }

        assertWithMessage("waitMillis should updated to error response wait seconds")
            .that(waitMillis.get()).isEqualTo(tryAfterSeconds * 1000)


        testScheduler.advanceTimeBy(
            RATE_LIMIT + 1000, TimeUnit.MILLISECONDS
        )
        observers[2].assertNotComplete().assertNoErrors().assertNoValues()
            .withTag("Request should be disposed.")

        assertThat(waitMillis.get()).isEqualTo(tryAfterSeconds * 1000)

        testScheduler.advanceTimeTo(0, TimeUnit.MILLISECONDS)

        val newRequest = breachRepository.searchForLeaks(queries.first()).test()

        testScheduler.advanceTimeBy(
            RATE_LIMIT + 2, TimeUnit.MILLISECONDS
        )
        newRequest.assertNotComplete().assertNoErrors().assertNoValues()
        testScheduler.advanceTimeBy(
            (tryAfterSeconds * 1000) - RATE_LIMIT + 2,
            TimeUnit.MILLISECONDS
        )
        newRequest.assertNoErrors().assertValue { it.size == 2 }

        assertThat(waitMillis.get()).isEqualTo(RATE_LIMIT)
    }


}

private fun getJson(
    fileName: String,
    path: String = "src/test/java/com/rmsr/myguard/data/remote/responses/json"
): String {
    return File("$path/$fileName.json").readText()
}
