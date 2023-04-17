package com.github.burachevsky.mqtthub.data.repository

import com.github.burachevsky.mqtthub.data.entity.CurrentIds

interface CurrentIdsRepository {

    suspend fun init()

    suspend fun getCurrentIds(): CurrentIds

    suspend fun getCurrentBrokerId(): Long?

    suspend fun getCurrentDashboardId(): Long?

    suspend fun updateCurrentBroker(brokerId: Long?)

    suspend fun updateCurrentDashboard(dashboardId: Long)
}