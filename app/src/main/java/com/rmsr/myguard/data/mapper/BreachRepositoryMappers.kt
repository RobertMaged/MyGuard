package com.rmsr.myguard.data.mapper

import com.rmsr.myguard.data.database.entity.BreachEntity
import com.rmsr.myguard.data.database.entity.QueryEntity
import com.rmsr.myguard.data.remote.pojo_response.BreachResponse
import com.rmsr.myguard.domain.entity.Breach
import com.rmsr.myguard.domain.entity.SearchQuery
import javax.inject.Inject

data class BreachRepositoryMappers @Inject constructor(
    val breachResponse: Mapper<BreachResponse, BreachEntity>,
    val breachEntity: ReversibleMapper<BreachEntity, Breach>,
    val queryEntity: ReversibleMapper<QueryEntity, SearchQuery>
)
