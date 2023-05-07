package com.github.burachevsky.mqtthub.core.data.repository.impl

import com.github.burachevsky.mqtthub.core.data.mapper.asEntity
import com.github.burachevsky.mqtthub.core.data.mapper.asModel
import com.github.burachevsky.mqtthub.core.data.repository.TileRepository
import com.github.burachevsky.mqtthub.core.data.repository.impl.cache.TileMemoryCache
import com.github.burachevsky.mqtthub.core.database.dao.TileDao
import com.github.burachevsky.mqtthub.core.model.Tile
import com.github.burachevsky.mqtthub.core.model.TopicUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TileRepositoryImpl @Inject constructor(
    private val tileDao: TileDao,
    private val memoryCache: TileMemoryCache,
) : TileRepository {

    private suspend fun <T> withMemoryCache(action: suspend (TileMemoryCache).() -> T): T {
        return memoryCache.apply(
            initializer = {
                tileDao.getAllTiles().map {
                    it.asModel()
                }
            },
            action = action
        )
    }

    override suspend fun insertTile(tile: Tile): Tile {
        val id = tileDao.insert(tile.asEntity())
        val inserted = tile.copy(id = id)
        withMemoryCache {
            insert(inserted)
        }
        return inserted
    }

    override suspend fun updateTile(tile: Tile) {
        tileDao.update(tile.asEntity())
        withMemoryCache {
            update(tile)
        }
    }

    override suspend fun updateTiles(tiles: List<Tile>) {
        tileDao.update(tiles.map { it.asEntity() })
        withMemoryCache {
            update(tiles)
        }
    }

    override suspend fun deleteTile(id: Long) {
        tileDao.delete(id)
        withMemoryCache {
            delete(id)
        }
    }

    override suspend fun deleteTiles(tiles: List<Tile>) {
        tileDao.delete(tiles.map { it.asEntity() })
        withMemoryCache {
            delete(tiles.map { it.id })
        }
    }

    override suspend fun updatePayloadAndGetTilesToNotify(
        subscribeTopic: String,
        payload: String
    ): List<Tile> {
        tileDao.updatePayload(subscribeTopic, payload)
        return withMemoryCache {
            updatePayloadAndGetTilesToNotify(subscribeTopic, payload)
        }
    }

    override suspend fun getTile(id: Long): Tile {
        return withMemoryCache {
            getById(id)
        }
    }

    override suspend fun getDashboardTiles(dashboardId: Long): List<Tile> {
        return withMemoryCache {
            getDashboardTiles(dashboardId)
        }
    }

    override suspend fun getAllTiles(): List<Tile> {
        return withMemoryCache {
            getAll()
        }
    }

    override fun observeTile(id: Long): Flow<Tile> {
        return tileDao.observeTile(id).map { it.asModel() }
    }

    override fun observeTopicUpdates(): Flow<TopicUpdate> {
        return memoryCache.observeTopicUpdates()
    }
}