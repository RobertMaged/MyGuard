package com.rmsr.myguard.data.mapper

import com.rmsr.myguard.data.database.entity.BreachEntity
import com.rmsr.myguard.domain.entity.Breach
import java.time.LocalDate
import javax.inject.Inject

class BreachEntityMapper @Inject constructor() :
    ReversibleMapper<BreachEntity, Breach> {

    override fun map(input: BreachEntity): Breach {
        val meta = Breach.Metadata(
            logoUrl = input.logoPath, domain = input.domain, createdTime = input.createdTime,
        )
        val info = Breach.LeakInfo(
            description = input.description,
            pwnCount = input.pwnCount,
            discoveredDate = LocalDate.parse(input.date),
            compromisedData = input.compromisedData
        )

        return Breach(
            id = input.id,
            title = input.title,
            metadata = meta,
            leakInfo = info
        )
    }

    override fun mapReversed(input: Breach): BreachEntity {
        return BreachEntity(
            id = input.id,
            //if needed access dao first
            leakName = "",
            createdTime = input.metadata.createdTime,
            logoPath = input.metadata.logoUrl,
            title = input.title,
            description = input.leakInfo.description,
            domain = input.metadata.domain,
            date = input.leakInfo.discoveredDate.toString(),
            pwnCount = input.leakInfo.pwnCount,
            compromisedData = input.leakInfo.compromisedData
        )
    }

}