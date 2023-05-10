package com.github.burachevsky.mqtthub.core.data.repository.impl

import com.github.burachevsky.mqtthub.core.common.Converter
import com.github.burachevsky.mqtthub.core.data.mapper.asEntity
import com.github.burachevsky.mqtthub.core.data.mapper.asModel
import com.github.burachevsky.mqtthub.core.data.repository.DashboardRepository
import com.github.burachevsky.mqtthub.core.db.dao.DashboardDao
import com.github.burachevsky.mqtthub.core.db.entity.DashboardEntity
import com.github.burachevsky.mqtthub.core.db.entity.DashboardWithTiles
import com.github.burachevsky.mqtthub.core.model.Dashboard
import com.github.burachevsky.mqtthub.core.model.Tile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val dashboardDao: DashboardDao,
): DashboardRepository {

    private suspend fun insertDashboard(dashboard: DashboardEntity): DashboardEntity {
        val id = dashboardDao.insert(dashboard)
        return dashboard.copy(id = id)
    }

    override suspend fun insertDashboard(dashboard: Dashboard): Dashboard {
        val id = dashboardDao.insert(dashboard.asEntity())
        return dashboard.copy(id = id)
    }

    override suspend fun deleteDashboard(id: Long) {
        return dashboardDao.delete(id)
    }

    override suspend fun updateDashboard(dashboard: Dashboard) {
        return dashboardDao.update(dashboard.asEntity())
    }

    override suspend fun updateDashboardName(dashboardId: Long, name: String) {
        return dashboardDao.updateDashboardName(dashboardId, name)
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

        return Converter.toJson(dashboardWithTiles)
    }

    override suspend fun importDashboardFromJson(json: String): Pair<Long, List<Tile>> {
        val importedDashboardWithTiles = Converter.fromJson<DashboardWithTiles>(json)
        val dashboard = insertDashboard(importedDashboardWithTiles.dashboard)

        val importedTiles = importedDashboardWithTiles.tiles.map {
            it.copy(dashboardId =  dashboard.id).asModel()
        }

        return dashboard.id to importedTiles
    }

    override fun observeDashboards(): Flow<List<Dashboard>> {
        return dashboardDao.observeAll().map { entities ->
            entities.map { it.asModel() }
        }
    }

    override fun observeDashboard(id: Long): Flow<Dashboard?> {
        return dashboardDao.observe(id).map { it?.asModel() }
    }

    private suspend fun getDashboardWithTiles(id: Long): DashboardWithTiles {
        return dashboardDao.getDashboardWithTiles(id).run {
            copy(tiles = tiles.sortedBy { it.dashboardPosition })
        }
    }
}