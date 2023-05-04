package com.github.burachevsky.mqtthub.data.repository

import android.content.Context
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.data.Converters
import com.github.burachevsky.mqtthub.data.dao.DashboardDao
import com.github.burachevsky.mqtthub.data.entity.Dashboard
import com.github.burachevsky.mqtthub.data.entity.DashboardWithTiles
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val dashboardDao: DashboardDao,
    private val applicationContext: Context,
    private val currentIdsRepository: CurrentIdsRepository,
    private val tileRepository: TileRepository,
): DashboardRepository {

    override suspend fun insertDashboard(dashboard: Dashboard): Dashboard {
        val id = dashboardDao.insert(dashboard)
        return dashboard.copy(id = id)
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

    override fun observeCurrentDashboard(): Flow<Dashboard> {
        return channelFlow {
            coroutineScope {
                var job: Job? = null
                var currentId: Long

                currentIdsRepository
                    .observeCurrentDashboardId()
                    .distinctUntilChanged()
                    .collect { id ->
                        job?.cancel()

                        currentId = id ?: insertDashboard(createFirstDashboard()).id

                        job = launch {
                            dashboardDao.observeDashboard(currentId)
                                .collect { dashboard ->
                                    if (isActive && id == currentId) {
                                        send(dashboard)
                                    }
                                }
                        }
                    }
            }
        }
    }

    override suspend fun updateDashboardName(dashboardId: Long, name: String) {
        return dashboardDao.updateDashboardName(dashboardId, name)
    }

    override fun observeDashboards(): Flow<List<Dashboard>> {
        return dashboardDao.observeDashboards()
    }

    override suspend fun getDashboardWithTiles(id: Long): DashboardWithTiles {
        return dashboardDao.getDashboardWithTiles(id).run {
            copy(tiles = tiles.sortedBy { it.dashboardPosition })
        }
    }

    override suspend fun packDashboardForExport(id: Long): String {
        val dashboardWithTiles = getDashboardWithTiles(id).run {
            copy(
                dashboard = dashboard.copy(id = 0),
                tiles = tiles.map {
                    it.copy(id = 0, dashboardId = 0, payload = "")
                }
            )
        }

        return Gson().toJson(dashboardWithTiles)
    }

    override suspend fun importDashboardFromJson(json: String): Dashboard {
        val importedDashboardWithTiles = Converters.fromJson<DashboardWithTiles>(json)

        val dashboard = insertDashboard(importedDashboardWithTiles.dashboard)

        importedDashboardWithTiles.tiles.forEach { tile ->
            tileRepository.insertTile(tile.copy(dashboardId = dashboard.id))
        }

        return dashboard
    }

    private fun createFirstDashboard(): Dashboard {
        return Dashboard(
            name = applicationContext.getString(R.string.default_dashboard_name)
        )
    }
}