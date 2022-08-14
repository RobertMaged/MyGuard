package com.rmsr.myguard.data.remote

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rmsr.myguard.data.database.FakeBreachDao
import com.rmsr.myguard.data.mapper.BreachResponseMapper
import com.rmsr.myguard.data.remote.pojo_response.BreachResponse
import com.rmsr.myguard.utils.RxJavaSchedulersRule
import com.rmsr.myguard.utils.RxTestSchedulers
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.TestScheduler
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class BreachRemoteDataSourceInitFacadeTest {
    private val testScheduler = TestScheduler()

    @get:Rule
    var rxRule = RxJavaSchedulersRule(testScheduler)

    private val rxSchedulers = RxTestSchedulers(testScheduler)

    private val responseMapper = BreachResponseMapper()
    private lateinit var breachDao: FakeBreachDao
    private lateinit var apiFacade: BreachRemoteDataSourceInitFacade

    private val allBreachesJson: String = getJson("all_breaches")

    companion object {
        private lateinit var breachRemoteSource: BreachesRemoteDataSource

        @MockK
        private lateinit var fire: IFire

        private lateinit var server: MockWebServer

        @JvmStatic
        @BeforeClass
        fun init() {
            MockKAnnotations.init(this)

            server = MockWebServer().apply {

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

            }

            every { fire.initAppApis() } returns Completable.complete()
            every { fire.ensureUserSignedIn() } returns Completable.complete()


        }

        @JvmStatic
        @AfterClass
        fun tearDown() {
            server.shutdown()
        }
    }

    @Before
    fun startUp() {
        breachDao = FakeBreachDao()
        apiFacade = BreachRemoteDataSourceInitFacade(
            { breachRemoteSource },
            breachDao, responseMapper,
            fire,
            rxSchedulers
        )

    }

    private val normalAllBreachesResponse = MockResponse().apply {
        setResponseCode(200)
        setBody(allBreachesJson)
    }

    @Test
    fun `make sure api Facade refreshed`() {
        server.enqueue(normalAllBreachesResponse)

        val observer = apiFacade.initIfNot().test()


        val request = server.takeRequest()

        assertThat(request.path).isEqualTo(BreachesRemoteDataSource.ALL_BREACHES)
        testScheduler.triggerActions()
        observer
            .assertNoErrors()
            .assertComplete()

        val s = Gson().fromJson<List<BreachResponse>>(
            allBreachesJson,
            object : TypeToken<List<BreachResponse>>() {}.type
        )

        assertThat(breachDao.db.toList()).containsExactlyElementsIn(
            responseMapper.map(s)
        )
    }


    private fun getJson(
        fileName: String,
        path: String = "src/test/java/com/rmsr/myguard/data/remote/responses/json"
    ): String {
        return File("$path/$fileName.json").readText()
    }
}
