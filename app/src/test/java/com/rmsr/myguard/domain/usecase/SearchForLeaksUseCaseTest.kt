package com.rmsr.myguard.domain.usecase

import com.rmsr.myguard.data.repository.ErrorHandleImpl
import com.rmsr.myguard.domain.entity.Breach
import com.rmsr.myguard.domain.entity.SearchQuery
import com.rmsr.myguard.domain.entity.errors.InvalidSearchQueryException
import com.rmsr.myguard.domain.repository.BreachRepository
import com.rmsr.myguard.domain.usecase.breaches.SearchForLeaksUseCase
import com.rmsr.myguard.utils.MyLog
import com.rmsr.myguard.utils.RxJavaSchedulersRule
import com.rmsr.myguard.utils.onEmptyListComplete
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.reactivex.rxjava3.core.Maybe
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate


class SearchForLeaksUseCaseTest {

    @get:Rule
    var rxRule = RxJavaSchedulersRule()

    companion object {
        private val breaches = List(50) {
            Breach(
                id = it.toLong(),
                title = "Test$it, Title",
                metadata = Breach.Metadata(
                    logoUrl = "",
                    domain = "Test$it.Domain.com",
                    createdTime = 0,
                ),
                leakInfo = Breach.LeakInfo(
                    description = "",
                    discoveredDate = LocalDate.MIN,
                    pwnCount = 0,
                    compromisedData = emptyList()
                ),
            )
        }

        private val domain: (String) -> Maybe<List<Breach>> = { x ->
            Maybe.just(breaches.filter { b -> b.metadata.domain.contains(x, true) })
                .onEmptyListComplete()
        }

        private val title: (String) -> Maybe<List<Breach>> = { x ->
            Maybe.just(breaches.filter { b ->
                b.title.contains(x, true) || b.metadata.domain.contains(
                    x,
                    true
                )
            }).onEmptyListComplete()
        }

        private val random: () -> Maybe<List<Breach>> = { Maybe.just(breaches.shuffled()) }
        private val first5: () -> Maybe<List<Breach>> = { Maybe.just(breaches.take(5)) }

        @MockK
        private lateinit var breachRepo: BreachRepository

        @MockK(relaxed = true, relaxUnitFun = true)
        private lateinit var log: MyLog

        @JvmStatic
        @BeforeClass
        fun init() {
            MockKAnnotations.init(this)
//            every { breachRepo.findDomainBreaches(any()) } answers { domain(firstArg()) }
//            every { breachRepo.findDomainNameBreaches(any()) } answers { title(firstArg()) }
//            every { breachRepo.findEmailBreaches(any()) } returns first5()
//            every { breachRepo.findPhoneBreaches(any()) } returns first5()

            every {
                breachRepo.searchForLeaks(query = any())
            } answers {
                when (val query = firstArg<SearchQuery>()) {
                    is SearchQuery.Domain -> domain(query.query)
                    is SearchQuery.DomainName -> title(query.query)
                    is SearchQuery.Email -> first5()
                    is SearchQuery.Phone -> first5()
                }
            }
        }
    }

    private val breachUseCase =
        SearchForLeaksUseCase(breachRepo, ValidateSearchQueryUseCase(), ErrorHandleImpl())

    @Test
    fun `given right formatted email return founded breach List`() {
        val breachQuery = SearchQuery.Email("test@gmail.com")
        breachUseCase.searchForLeaks(breachQuery)
            .test()
            .assertNoErrors()
            .assertValue(breaches.take(5))
    }

    @Test()//expected = InvalidEmailException::class)
    fun `given wrong formatted email return wrong mail exception`() {
        val breachQuery = SearchQuery.Email("test@gmail.c")
        breachUseCase.searchForLeaks(breachQuery)
            .test()
            .assertNotComplete()
            .assertNoValues()
            .assertError(InvalidSearchQueryException.InvalidEmailException::class.java)
    }

    @Test
    fun `given right phone number return founded breach List`() {
        val breachQuery = SearchQuery.Phone("201124584842")
        breachUseCase.searchForLeaks(breachQuery)
            .test()
            .assertNoErrors()
            .assertValue(breaches.take(5))
    }

    @Test()//expected = InvalidPhoneNumberException::class)
    fun `given wrong phone number return wrong phone exception`() {
        val breachQuery = SearchQuery.Phone("01124584")
        breachUseCase.searchForLeaks(breachQuery)
            .test()
            .assertNotComplete()
            .assertNoValues()
            .assertError(InvalidSearchQueryException.InvalidPhoneNumberException::class.java)
    }

    @Test
    fun `given right domain return founded breach List`() {
        val breachQuery = SearchQuery.Domain("test3.domain.com")
        breachUseCase.searchForLeaks(breachQuery)
            .test()
            .assertNoErrors()
            .assertValue(breaches.filter {
                it.metadata.domain.contains(
                    "test3.domain.com",
                    ignoreCase = true
                )
            })
    }

    @Test()//expected = InvalidDomainException::class)
    fun `given wrong domain return wrong domain exception`() {
        val breachQuery = SearchQuery.Domain("test3@domain.")
        breachUseCase.searchForLeaks(breachQuery)
            .test()
            .assertNotComplete()
            .assertNoValues()
            .assertError(InvalidSearchQueryException.InvalidDomainException::class.java)
    }

    @Test
    fun `given right domainName return founded breach List`() {
        val breachQuery = SearchQuery.DomainName("test3")
        breachUseCase.searchForLeaks(breachQuery)
            .test()
            .assertNoErrors()
            .assertValue(breaches.filter {
                it.title.contains(
                    "test3",
                    ignoreCase = true
                ) || it.metadata.domain.contains("test3", ignoreCase = true)
            })
    }

    @Test()//expected = InvalidDomainNameException::class)
    fun `given wrong domainName return wrong domain exception`() {
        val breachQuery = SearchQuery.DomainName("test3@domain.com")
        breachUseCase.searchForLeaks(breachQuery)
            .test()
            .assertNotComplete()
            .assertNoValues()
            .assertError(InvalidSearchQueryException.InvalidDomainNameException::class.java)
    }


}