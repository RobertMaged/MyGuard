package com.rmsr.myguard.domain.usecase.breaches

import com.google.common.truth.Truth.assertThat
import com.rmsr.myguard.domain.entity.Breach
import org.junit.BeforeClass
import org.junit.Test
import java.time.LocalDate
import kotlin.random.Random

class SortBreachesUseCaseTest {

    val sortBreaches = SortBreachesUseCase()

    companion object {

        private lateinit var breachesListSortedAscending: List<Breach>


        @BeforeClass
        @JvmStatic
        fun setupList() {
            var epochDayOf2015 = 1420070400L

            breachesListSortedAscending = List(3000) {

                val date = LocalDate.ofEpochDay(epochDayOf2015)

                epochDayOf2015 += 86400

                return@List createBreach(
                    title = "Test${date.year % 2015}",
                    date = date,
                    pwnCount = date.toEpochDay().toInt()
                )
            }


        }

        private fun createBreach(
            title: String = "",
            date: LocalDate = LocalDate.MIN,
            pwnCount: Int = 0
        ) = Breach.EMPTY.let {
            it.copy(
                title = title,
                leakInfo = it.leakInfo.copy(
                    discoveredDate = date, pwnCount = pwnCount
                )
            )
        }

    }

    /////////////// Title ////////////////

    @Test
    fun `sort(by Title, Ascending) unsorted breachesList`() {
        val breachesList = mutableListOf<Breach>().apply {
            for (i in 'a'..'z')
                add(createBreach(title = "${i}Test Name"))
        }

        val sortedList = sortBreaches.byTitleAscending(breachesList.shuffled())
        assertThat(sortedList).containsExactlyElementsIn(breachesList).inOrder()
    }

    @Test
    fun `sort(by Title, Descending) unsorted breachesList`() {
        val breachesList = mutableListOf<Breach>().apply {
            for (i in 'a'..'z')
                add(createBreach(title = "${i}Test Name"))
        }

        val sortedList = sortBreaches.byTitleDescending(breachesList.shuffled())
        assertThat(sortedList).containsExactlyElementsIn(breachesList.reversed()).inOrder()
    }

    /////////////// Date ////////////////

    @Test
    fun `sort(by DATE, Ascending) unsorted breachesList`() {
        val sortedList =
            sortBreaches.byDateAscending(breachesListSortedAscending.shuffled())

        val randomIndex = List(20) { Random.nextInt(1, sortedList.size) }
        randomIndex.forEach {
            assertThat(sortedList[it].leakInfo.discoveredDate).isLessThan(sortedList[it + 1].leakInfo.discoveredDate)
            assertThat(sortedList[it].leakInfo.discoveredDate).isGreaterThan(sortedList[it - 1].leakInfo.discoveredDate)
        }

        assertThat(sortedList).containsExactlyElementsIn(breachesListSortedAscending).inOrder()
    }

    @Test
    fun `sort(by Date, descending) unsorted breachesList`() {
        val sortedList =
            sortBreaches.byDateDescending(breachesListSortedAscending.shuffled())

        val randomIndex = List(20) { Random.nextInt(1, sortedList.size - 1) }
        randomIndex.forEach {
            assertThat(sortedList[it].leakInfo.discoveredDate).isGreaterThan(sortedList[it + 1].leakInfo.discoveredDate)
            assertThat(sortedList[it].leakInfo.discoveredDate).isLessThan(sortedList[it - 1].leakInfo.discoveredDate)
        }

        assertThat(sortedList).containsExactlyElementsIn(breachesListSortedAscending.reversed())
            .inOrder()
    }


    /////////////// Pwn Count ////////////////

    @Test
    fun `sort(by PwnCount, Ascending) unsorted breachesList`() {
        val sortedList =
            sortBreaches.byPwnCountAscending(breachesListSortedAscending.shuffled())

        val randomIndex = List(20) { Random.nextInt(1, sortedList.size) }
        randomIndex.forEach {
            assertThat(sortedList[it].leakInfo.pwnCount).isLessThan(sortedList[it + 1].leakInfo.pwnCount)
            assertThat(sortedList[it].leakInfo.pwnCount).isGreaterThan(sortedList[it - 1].leakInfo.pwnCount)
        }
        assertThat(sortedList).containsExactlyElementsIn(breachesListSortedAscending).inOrder()
    }

    @Test
    fun `sort(by PwnCount, Descending) unsorted breachesList`() {
        val sortedList = sortBreaches.byPwnCountDescending(breachesListSortedAscending.shuffled())

        val randomIndex = List(20) { Random.nextInt(1, sortedList.size - 1) }
        randomIndex.forEach {
            assertThat(sortedList[it].leakInfo.pwnCount).isGreaterThan(sortedList[it + 1].leakInfo.pwnCount)
            assertThat(sortedList[it].leakInfo.pwnCount).isLessThan(sortedList[it - 1].leakInfo.pwnCount)
        }
        assertThat(sortedList).containsExactlyElementsIn(breachesListSortedAscending.reversed())
            .inOrder()
    }
}