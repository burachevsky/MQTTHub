package com.github.burachevsky.mqtthub.data.repository

import com.github.burachevsky.mqtthub.data.entity.Tile

interface TileRepository {

    suspend fun insertTile(tile: Tile): Tile

    suspend fun updateTile(tile: Tile)

    suspend fun updateTiles(tiles: List<Tile>)

    suspend fun deleteTile(id: Long)

    suspend fun deleteTiles(tiles: List<Tile>)

    suspend fun updatePayload(dashboardId: Long, subscribeTopic: String, payload: String)

    suspend fun getTile(id: Long): Tile
}