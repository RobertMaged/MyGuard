package com.rmsr.myguard.data.mapper

import com.rmsr.myguard.data.database.entity.QueryEntity
import com.rmsr.myguard.domain.entity.QueryType
import com.rmsr.myguard.domain.entity.SearchQuery
import javax.inject.Inject

class QueryEntityMapper @Inject constructor() : ReversibleMapper<QueryEntity, SearchQuery> {

    override fun map(input: QueryEntity): SearchQuery = when (input.type) {
        QueryType.EMAIL -> SearchQuery.Email(
            query = input.content,
            hint = input.hint
        )
        QueryType.Phone -> SearchQuery.Phone(
            query = input.content,
            hint = input.hint
        )
        QueryType.DOMAIN -> SearchQuery.Domain(
            query = input.content,
            hint = input.hint
        )
        QueryType.DOMAIN_NAME -> SearchQuery.DomainName(
            query = input.content,
            hint = input.hint
        )
    }

    override fun mapReversed(input: SearchQuery): QueryEntity {
        val (query, queryType, queryHint) = input
        return QueryEntity(content = query, type = queryType, hint = queryHint)
    }
}