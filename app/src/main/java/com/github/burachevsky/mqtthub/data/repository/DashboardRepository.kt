package com.github.burachevsky.mqtthub.data.repository

import com.github.burachevsky.mqtthub.data.entity.Dashboard
import com.github.burachevsky.mqtthub.data.entity.DashboardWithTiles

interface DashboardRepository {

    suspend fun insertDashboard(dashboard: Dashboard): Dashboard

    suspend fun deleteDashboard(id: Long)

    suspend fun updateDashboard(dashboard: Dashboard)

    suspend fun getDashboards(): List<Dashboard>

    suspend fun getDashboardWithTiles(id: Long): DashboardWithTiles

    suspend fun getCurrentDashboardWithTiles(): DashboardWithTiles

    suspend fun packDashboardForExport(id: Long): String

    suspend fun importDashboardFromJson(json: String): Dashboard
}