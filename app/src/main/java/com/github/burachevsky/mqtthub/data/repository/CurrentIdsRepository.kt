package com.github.burachevsky.mqtthub.data.repository

import com.github.burachevsky.mqtthub.data.entity.CurrentIds
import kotlinx.coroutines.flow.Flow

interface CurrentIdsRepository {

    suspend fun init()

    suspend fun getCurrentIds(): CurrentIds

    fun observeCurrentIds(): Flow<CurrentIds>

    fun observeCurrentDashboardId(): Flow<Long?>

    fun observeCurrentBrokerId(): Flow<Long?>

    suspend fun getCurrentBrokerId(): Long?

    suspend fun getCurrentDashboardId(): Long?

    suspend fun updateCurrentBroker(brokerId: Long?)

    suspend fun updateCurrentDashboard(dashboardId: Long)
}