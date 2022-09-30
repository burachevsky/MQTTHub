package com.github.burachevsky.mqtthub.data.repository

import com.github.burachevsky.mqtthub.data.entity.Tile

interface TileRepository {

    suspend fun getAllBrokerTiles(brokerId: Long): List<Tile>

    suspend fun insertTile(tile: Tile): Tile

    suspend fun updateTile(tile: Tile)

    suspend fun deleteTile(id: Long)
}