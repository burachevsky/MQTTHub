package com.github.burachevsky.mqtthub.data.repository

import com.github.burachevsky.mqtthub.data.entity.Dashboard
import com.github.burachevsky.mqtthub.data.entity.DashboardWithTiles
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {

    suspend fun insertDashboard(dashboard: Dashboard): Dashboard

    suspend fun deleteDashboard(id: Long)

    suspend fun updateDashboard(dashboard: Dashboard)

    suspend fun getDashboards(): List<Dashboard>

    fun observeDashboards(): Flow<List<Dashboard>>

    fun observeCurrentDashboard(): Flow<Dashboard>

    suspend fun updateDashboardName(dashboardId: Long, name: String)

    suspend fun getDashboardWithTiles(id: Long): DashboardWithTiles

    suspend fun packDashboardForExport(id: Long): String

    suspend fun importDashboardFromJson(json: String): Dashboard
}