package com.rmsr.myguard.data.repository

import com.rmsr.myguard.data.database.dao.QueryDao
import com.rmsr.myguard.data.database.dao.SearchHistoryDao
import com.rmsr.myguard.data.database.entity.relations.HistoryWithQuery
import com.rmsr.myguard.data.mapper.Mapper
import com.rmsr.myguard.domain.entity._History
import com.rmsr.myguard.domain.repository._HistoryRepository
import com.rmsr.myguard.utils.RxSchedulers
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

@Suppress("Future not implemented or tested yet.")
class _HistoryRepositoryImpl @Inject constructor(
    private val queryDao: QueryDao,
    private val historyDao: SearchHistoryDao,
    private val mapper: Mapper<HistoryWithQuery, _History>,
    private val rxSchedulers: RxSchedulers,
) : _HistoryRepository {

    override fun observeHistory(): Flowable<List<_History>> {
        return historyDao.observeHistory()
            .subscribeOn(rxSchedulers.io())
            .distinctUntilChanged()
            .map { mapper.map(it) }
    }

}