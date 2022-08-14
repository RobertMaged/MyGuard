package com.rmsr.myguard.data.mapper

import com.rmsr.myguard.data.database.entity.QueryEntity
import com.rmsr.myguard.data.database.entity.ScheduleEntity
import com.rmsr.myguard.data.database.entity.relations.ScheduleWithQuery
import com.rmsr.myguard.domain.entity.QueryType
import com.rmsr.myguard.domain.entity.Schedule
import com.rmsr.myguard.domain.entity.SearchQuery
import javax.inject.Inject

class ScheduleWithQueryMapper @Inject constructor() :
    ReversibleMapper<ScheduleWithQuery, Schedule> {


    override fun map(input: ScheduleWithQuery): Schedule {
        return Schedule(
            id = input.scheduleEntity.id,
            isMuted = input.scheduleEntity.isMuted,
            createdTime = input.scheduleEntity.createdTime,
            searchQuery = when (input.queryEntity.type) {
                QueryType.EMAIL -> SearchQuery.Email(
                    query = input.queryEntity.content,
                    hint = input.queryEntity.hint
                )
                QueryType.Phone -> SearchQuery.Phone(
                    query = input.queryEntity.content,
                    hint = input.queryEntity.hint
                )
                QueryType.DOMAIN -> SearchQuery.Domain(
                    query = input.queryEntity.content,
                    hint = input.queryEntity.hint
                )
                QueryType.DOMAIN_NAME -> SearchQuery.DomainName(
                    query = input.queryEntity.content,
                    hint = input.queryEntity.hint
                )
            }
        )
    }

    override fun mapReversed(input: Schedule): ScheduleWithQuery {
        return ScheduleWithQuery(
            scheduleEntity = ScheduleEntity(
                id = input.id,
                queryId = 0,
                isMuted = input.isMuted,
                createdTime = input.createdTime
            ),
            queryEntity = QueryEntity(
                id = 0,
                content = input.searchQuery.query, hint = input.searchQuery.hint,
                type = when (input.searchQuery) {
                    is SearchQuery.Domain -> QueryType.DOMAIN
                    is SearchQuery.DomainName -> QueryType.DOMAIN_NAME
                    is SearchQuery.Email -> QueryType.EMAIL
                    is SearchQuery.Phone -> QueryType.Phone
                }
            )
        )
    }
}