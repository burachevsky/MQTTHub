package com.github.burachevsky.mqtthub.data.repository

import com.github.burachevsky.mqtthub.data.dao.TileDao
import com.github.burachevsky.mqtthub.data.entity.Tile
import javax.inject.Inject

class TileRepositoryImpl @Inject constructor(
    private val tileDao: TileDao,
) : TileRepository {
    override suspend fun getAllBrokerTiles(brokerId: Long): List<Tile> {
        return tileDao.getAllBrokerTiles(brokerId)
    }

    override suspend fun insertTile(tile: Tile): Tile {
        val id = tileDao.insert(tile)
        return tile.copy(id = id)
    }

    override suspend fun updateTile(tile: Tile) {
        tileDao.update(tile)
    }

    override suspend fun deleteTile(id: Long) {
        tileDao.delete(id)
    }

    override suspend fun updatePayload(brokerId: Long, subscribeTopic: String, payload: String) {
        return tileDao.updatePayload(brokerId, subscribeTopic, payload)
    }

    override suspend fun getTile(id: Long): Tile {
        return tileDao.getById(id)
    }
}