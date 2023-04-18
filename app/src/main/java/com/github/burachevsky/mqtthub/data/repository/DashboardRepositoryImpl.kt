package com.github.burachevsky.mqtthub.data.repository

import android.content.Context
import androidx.room.Transaction
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.data.dao.DashboardDao
import com.github.burachevsky.mqtthub.data.entity.Dashboard
import com.github.burachevsky.mqtthub.data.entity.DashboardWithTiles
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val dashboardDao: DashboardDao,
    private val applicationContext: Context,
    private val currentIdsRepository: CurrentIdsRepository,
): DashboardRepository {

    override suspend fun insertDashboard(dashboard: Dashboard): Dashboard {
        val id = dashboardDao.insert(dashboard)
        return dashboard.copy(id = id)
    }

    @Transaction
    override suspend fun insertDashboardLast(dashboard: Dashboard): Dashboard {
        val position = dashboardDao.count()
        val id = dashboardDao.insert(dashboard.copy(position = position))
        return dashboard.copy(id = id, position = position)
    }

    override suspend fun deleteDashboard(id: Long) {
        return dashboardDao.delete(id)
    }

    override suspend fun updateDashboard(dashboard: Dashboard) {
        return dashboardDao.update(dashboard)
    }

    override suspend fun getDashboards(): List<Dashboard> {
        return dashboardDao.getAll()
    }

    override suspend fun getDashboardWithTiles(id: Long): DashboardWithTiles {
        return dashboardDao.getDashboardWithTiles(id).run {
            copy(tiles = tiles.sortedBy { it.dashboardPosition })
        }
    }

    @Transaction
    override suspend fun getCurrentDashboardWithTiles(): DashboardWithTiles {
        var currentDashboardId = currentIdsRepository.getCurrentDashboardId()

        if (currentDashboardId == null) {
            val dashboard = insertDashboard(
                Dashboard(
                    name = applicationContext.getString(R.string.default_dashboard_name),
                    position = 0
                )
            )

            currentDashboardId = dashboard.id
            currentIdsRepository.updateCurrentDashboard(currentDashboardId)
        }

        return getDashboardWithTiles(currentDashboardId)
    }

    @Transaction
    override suspend fun getCurrentDashboard(): Dashboard {
        val dashboard = if (dashboardDao.count() == 0) {
            insertDashboard(
                Dashboard(
                    name = applicationContext.getString(R.string.default_dashboard_name),
                    position = 0
                )
            )
        } else {
            dashboardDao.getFirstDashboard()
        }

        return dashboard
    }
}