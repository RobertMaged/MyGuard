package com.rmsr.myguard.domain.usecase.breaches

import com.rmsr.myguard.domain.entity.Breach
import javax.inject.Inject

class SortBreachesUseCase @Inject constructor() {

    fun byDateAscending(breachesToSort: List<Breach>): List<Breach> =
        breachesToSort.sortedBy { it.leakInfo.discoveredDate }

    fun byDateDescending(breachesToSort: List<Breach>): List<Breach> =
        breachesToSort.sortedByDescending { it.leakInfo.discoveredDate }

    fun byTitleAscending(breachesToSort: List<Breach>): List<Breach> =
        breachesToSort.sortedBy { it.title }

    fun byTitleDescending(breachesToSort: List<Breach>): List<Breach> =
        breachesToSort.sortedByDescending { it.title }

    fun byPwnCountAscending(breachesToSort: List<Breach>): List<Breach> =
        breachesToSort.sortedBy { it.leakInfo.pwnCount }

    fun byPwnCountDescending(breachesToSort: List<Breach>): List<Breach> =
        breachesToSort.sortedByDescending { it.leakInfo.pwnCount }

}