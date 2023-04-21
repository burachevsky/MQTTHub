package com.github.burachevsky.mqtthub.data.repository

import com.github.burachevsky.mqtthub.data.dao.TileDao
import com.github.burachevsky.mqtthub.data.entity.Tile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TileRepositoryImpl @Inject constructor(
    private val tileDao: TileDao,
) : TileRepository {

    override suspend fun insertTile(tile: Tile): Tile {
        val id = tileDao.insert(tile)
        return tile.copy(id = id)
    }

    override suspend fun updateTile(tile: Tile) {
        tileDao.update(tile)
    }

    override suspend fun updateTiles(tiles: List<Tile>) {
        tileDao.update(tiles)
    }

    override suspend fun deleteTile(id: Long) {
        tileDao.delete(id)
    }

    override suspend fun deleteTiles(tiles: List<Tile>) {
        tileDao.delete(tiles)
    }

    override suspend fun updatePayload(dashboardId: Long, subscribeTopic: String, payload: String) {
        return tileDao.updatePayload(dashboardId, subscribeTopic, payload)
    }

    override suspend fun getTile(id: Long): Tile {
        return tileDao.getById(id)
    }

    override fun observeTile(id: Long): Flow<Tile> {
        return tileDao.observeTile(id)
    }
}