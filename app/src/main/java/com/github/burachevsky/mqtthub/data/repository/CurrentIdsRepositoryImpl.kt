package com.github.burachevsky.mqtthub.data.repository

import com.github.burachevsky.mqtthub.data.dao.CurrentIdsDao
import com.github.burachevsky.mqtthub.data.entity.CurrentIds
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CurrentIdsRepositoryImpl @Inject constructor(
    private val currentIdsDao: CurrentIdsDao,
) : CurrentIdsRepository {

    override suspend fun init() {
        return currentIdsDao.init()
    }

    override suspend fun getCurrentIds(): CurrentIds {
        return currentIdsDao.getCurrentIds()
    }

    override fun observeCurrentIds(): Flow<CurrentIds> {
        return currentIdsDao.observeCurrentIds()
    }

    override fun observeCurrentDashboardId(): Flow<Long?> {
        return currentIdsDao.observeCurrentDashboardId()
    }

    override fun observeCurrentBrokerId(): Flow<Long?> {
        return currentIdsDao.observeCurrentBrokerId()
    }

    override suspend fun getCurrentBrokerId(): Long? {
        return currentIdsDao.getCurrentBrokerId()
    }

    override suspend fun getCurrentDashboardId(): Long? {
        return currentIdsDao.getCurrentDashboardId()
    }

    override suspend fun updateCurrentBroker(brokerId: Long?) {
        return currentIdsDao.updateCurrentBrokerId(brokerId)
    }

    override suspend fun updateCurrentDashboard(dashboardId: Long) {
        return currentIdsDao.updateCurrentDashboardId(dashboardId)
    }
}