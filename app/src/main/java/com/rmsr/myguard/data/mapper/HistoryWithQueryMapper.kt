package com.rmsr.myguard.data.mapper

import com.rmsr.myguard.data.database.entity.QueryEntity
import com.rmsr.myguard.data.database.entity.relations.HistoryWithQuery
import com.rmsr.myguard.domain.entity.SearchQuery
import com.rmsr.myguard.domain.entity._History
import javax.inject.Inject

class HistoryWithQueryMapper @Inject constructor(
    private val queryEntityMapper: ReversibleMapper<QueryEntity, SearchQuery>
) : Mapper<HistoryWithQuery, _History> {

    override fun map(input: HistoryWithQuery): _History {
        return _History(queryEntityMapper.map(input.queryEntity))
    }

}
