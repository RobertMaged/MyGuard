package com.rmsr.myguard.data.mapper

import com.google.common.truth.Truth
import com.rmsr.myguard.data.database.entity.BreachEntity
import com.rmsr.myguard.data.remote.pojo_response.BreachResponse
import org.junit.Before
import org.junit.Test

class BreachResponseMapperTest {
    private val breachMapper = BreachResponseMapper()
    private lateinit var breachResponse: BreachResponse

    @Before
    fun setup() {
        breachResponse = BreachResponse(name = "Test")
    }

    private fun BreachResponse.toBreachEntityForTest() = BreachEntity(
        leakName = this.name ?: "NoName",
        createdTime = 0L,
        logoPath = this.logoPath ?: "NoLogo",
        title = this.title ?: "NoTile",
        description = this.description ?: "NoDescription",
        domain = this.domain ?: "NoDomain",
        date = this.breachDateDMY ?: "NoDate",
        pwnCount = this.pwnCount ?: 0,
        compromisedData = this.dataClassesList ?: emptyList<String>()
    )


    @Test
    fun `map valid BreachResponse, return new breach`() {
        val mappedBreach = breachMapper.map(breachResponse)

        Truth.assertThat(mappedBreach).isEqualTo(breachResponse.toBreachEntityForTest())
    }

    @Test
    fun `map valid BreachResponse, source should not changed, return new breach`() {
        val oldBreachResponse = breachResponse.copy()
        val mappedBreach = breachMapper.map(breachResponse)

        Truth.assertThat(mappedBreach).isEqualTo(breachResponse.toBreachEntityForTest())
        Truth.assertThat(oldBreachResponse).isEqualTo(breachResponse)
    }

    @Test
    fun `map valid BreachResponseList, return new breachList`() {
        val entityList = List(5) {
            breachResponse.copy(name = "Test $it", logoPath = "Test Logo $it")
        }
        val mappedBreachList = breachMapper.map(entityList)

        Truth.assertThat(mappedBreachList).isEqualTo(entityList.map { it.toBreachEntityForTest() })
    }

    @Test
    fun `map valid BreachResponseList, source should not changed, return new breachList`() {
        val entityList = List(5) {
            breachResponse.copy(name = "Test $it", logoPath = "Test Logo $it")
        }
        val oldEntityList = entityList.map { it.copy() }
        val mappedBreachList = breachMapper.map(entityList)

        Truth.assertThat(mappedBreachList).isEqualTo(entityList.map { it.toBreachEntityForTest() })
        Truth.assertThat(oldEntityList).isEqualTo(entityList)
    }
}