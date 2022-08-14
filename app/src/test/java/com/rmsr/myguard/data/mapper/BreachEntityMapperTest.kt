package com.rmsr.myguard.data.mapper

import com.google.common.truth.Truth.assertThat
import com.rmsr.myguard.data.database.entity.BreachEntity
import com.rmsr.myguard.domain.entity.Breach
import org.junit.Before
import org.junit.Test
import java.time.LocalDate


class BreachEntityMapperTest {
    private val breachMapper = BreachEntityMapper()
    private lateinit var breachEntity: BreachEntity
    private lateinit var breach: Breach

    @Before
    fun setup() {
        breachEntity = BreachEntity(
            id = 12,
            leakName = "test",
            createdTime = 0,
            logoPath = "Test Logo",
            title = "",
            description = "",
            domain = "",
            date = "2020-12-31",
            pwnCount = 0,
            compromisedData = emptyList()
        )

        breach = Breach.EMPTY.let {
            val metadata = it.metadata.copy(logoUrl = "Test Logo")
            it.copy(title = "Test", metadata = metadata)
        }

    }

    private fun BreachEntity.toBreachForTest() = Breach(
        id = this.id,
        title = this.title,
        metadata = Breach.Metadata(
            logoUrl = this.logoPath,
            domain = this.domain,
            createdTime = this.createdTime,
        ),
        leakInfo = Breach.LeakInfo(
            description = this.description,
            discoveredDate = LocalDate.parse(this.date),
            pwnCount = this.pwnCount,
            compromisedData = this.compromisedData
        ),
    )


    @Test
    fun `map valid BreachEntity, return new breach`() {
        val mappedBreach = breachMapper.map(breachEntity)

        assertThat(mappedBreach).isEqualTo(breachEntity.toBreachForTest())
    }

    @Test
    fun `map valid BreachEntity, source should not changed, return new breach`() {
        val oldBreachEntity = breachEntity.copy()
        val mappedBreach = breachMapper.map(breachEntity)

        assertThat(mappedBreach).isEqualTo(breachEntity.toBreachForTest())
        assertThat(oldBreachEntity).isEqualTo(breachEntity)
    }

    @Test
    fun `map valid BreachEntityList, return new breachList`() {
        val entityList = List(5) {
            breachEntity.copy(leakName = "Test $it", logoPath = "Test Logo $it")
        }
        val mappedBreachList = breachMapper.map(entityList)

        assertThat(mappedBreachList).isEqualTo(entityList.map { it.toBreachForTest() })
    }

    @Test
    fun `map valid BreachEntityList, source should not changed, return new breachList`() {
        val entityList = List(5) {
            breachEntity.copy(leakName = "Test $it", logoPath = "Test Logo $it")
        }
        val oldEntityList = entityList.map { it.copy() }
        val mappedBreachList = breachMapper.map(entityList)

        assertThat(mappedBreachList).isEqualTo(entityList.map { it.toBreachForTest() })
        assertThat(oldEntityList).isEqualTo(entityList)
    }


    @Test
    fun `mapReversed() map valid Breach, return new breachEntity`() {
        val mappedBreachEntity = breachMapper.mapReversed(breach)

        assertThat(mappedBreachEntity.toBreachForTest()).isEqualTo(breach)
    }

    @Test
    fun `mapReversed() map valid Breach, source should not changed, return new breachEntity`() {
        val oldBreach = breach.copy()
        val mappedBreachEntity = breachMapper.mapReversed(breach)

        assertThat(mappedBreachEntity.toBreachForTest()).isEqualTo(breach)
        assertThat(oldBreach).isEqualTo(breach)
    }

    @Test
    fun `mapReversed() map valid BreachList, return new breachEntityList`() {
        val breachList = List(5) {
            breach.copy(
                title = "Test $it",
                metadata = breach.metadata.copy(logoUrl = "Test Logo $it")
            )
        }

        val mappedBreachList = breachMapper.mapReversed(breachList)

        assertThat(breachList).isEqualTo(mappedBreachList.map { it.toBreachForTest() })
    }

    @Test
    fun `mapReversed() map valid BreachList, source should not changed, return new breachEntityList`() {
        val breachList = List(5) {
            breach.copy(
                title = "Test $it",
                metadata = breach.metadata.copy(logoUrl = "Test Logo $it")
            )
        }

        val oldBreachList = breachList.map { it.copy() }

        val mappedBreachList = breachMapper.mapReversed(breachList)

        assertThat(breachList).isEqualTo(mappedBreachList.map { it.toBreachForTest() })
        assertThat(breachList).isEqualTo(oldBreachList)
    }

}