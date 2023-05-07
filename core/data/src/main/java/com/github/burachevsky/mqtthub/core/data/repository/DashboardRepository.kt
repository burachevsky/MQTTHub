package com.github.burachevsky.mqtthub.core.data.repository

import com.github.burachevsky.mqtthub.core.model.Dashboard
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {

    suspend fun insertDashboard(dashboard: Dashboard): Dashboard

    suspend fun deleteDashboard(id: Long)

    suspend fun updateDashboard(dashboard: Dashboard)

    suspend fun getDashboards(): List<Dashboard>

    fun observeDashboards(): Flow<List<Dashboard>>

    fun observeCurrentDashboard(): Flow<Dashboard>

    suspend fun updateDashboardName(dashboardId: Long, name: String)

    suspend fun packDashboardForExport(id: Long): String

    suspend fun importDashboardFromJson(json: String): Dashboard
}