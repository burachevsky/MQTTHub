package com.github.burachevsky.mqtthub.core.data.repository.impl

import com.github.burachevsky.mqtthub.core.common.Converter
import com.github.burachevsky.mqtthub.core.data.mapper.asEntity
import com.github.burachevsky.mqtthub.core.data.mapper.asModel
import com.github.burachevsky.mqtthub.core.data.repository.CurrentIdsRepository
import com.github.burachevsky.mqtthub.core.data.repository.DashboardRepository
import com.github.burachevsky.mqtthub.core.data.repository.TileRepository
import com.github.burachevsky.mqtthub.core.db.dao.DashboardDao
import com.github.burachevsky.mqtthub.core.db.entity.DashboardEntity
import com.github.burachevsky.mqtthub.core.db.entity.DashboardWithTiles
import com.github.burachevsky.mqtthub.core.model.Dashboard
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val dashboardDao: DashboardDao,
    private val currentIdsRepository: CurrentIdsRepository,
    private val tileRepository: TileRepository,
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

    override suspend fun getDashboards(): List<Dashboard> {
        return dashboardDao.getAll().map { it.asModel() }
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
                        currentIdsRepository.updateCurrentDashboard(currentId)

                        job = launch {
                            dashboardDao.observeDashboard(currentId)
                                .filterNotNull()
                                .onEach { dashboard ->
                                    if (isActive && id == currentId) {
                                        send(dashboard.asModel())
                                    }
                                }
                                .collect()
                        }
                    }
            }
        }
    }

    override suspend fun updateDashboardName(dashboardId: Long, name: String) {
        return dashboardDao.updateDashboardName(dashboardId, name)
    }

    override fun observeDashboards(): Flow<List<Dashboard>> {
        return dashboardDao.observeDashboards().map { entities ->
            entities.map { it.asModel() }
        }
    }

    private suspend fun getDashboardWithTiles(id: Long): DashboardWithTiles {
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
        val importedDashboardWithTiles = Converter.fromJson<DashboardWithTiles>(json)

        val dashboard = insertDashboard(importedDashboardWithTiles.dashboard)

        try {
            importedDashboardWithTiles.tiles.forEach { tile ->
                val tileModel = tile.copy(dashboardId =  dashboard.id).asModel()

                tileRepository.insertTile(tileModel)
            }
        } finally {
            currentIdsRepository.updateCurrentDashboard(dashboard.id)
        }

        return dashboard.asModel()
    }

    private fun createFirstDashboard(): DashboardEntity {
        return DashboardEntity(name = "Dashboard")
    }
}