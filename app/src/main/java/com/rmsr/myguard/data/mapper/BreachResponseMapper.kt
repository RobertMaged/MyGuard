package com.rmsr.myguard.data.mapper

import com.rmsr.myguard.data.database.entity.BreachEntity
import com.rmsr.myguard.data.remote.pojo_response.BreachResponse
import javax.inject.Inject

class BreachResponseMapper @Inject constructor() : Mapper<BreachResponse, BreachEntity> {

    override fun map(input: BreachResponse): BreachEntity {
        return BreachEntity(
            leakName = input.name ?: "NoName",
            createdTime = 0L,
            logoPath = input.logoPath ?: "NoLogo",
            title = input.title ?: "NoTile",
            description = input.description ?: "NoDescription",
            domain = input.domain ?: "NoDomain",
            date = input.breachDateDMY ?: "NoDate",
            pwnCount = input.pwnCount ?: 0,
            compromisedData = input.dataClassesList ?: emptyList<String>()
        )
    }

}