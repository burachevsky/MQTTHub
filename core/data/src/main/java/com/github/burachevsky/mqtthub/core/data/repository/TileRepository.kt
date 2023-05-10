package com.github.burachevsky.mqtthub.core.data.repository

import com.github.burachevsky.mqtthub.core.model.Tile
import kotlinx.coroutines.flow.Flow

interface TileRepository {

    suspend fun insertTile(tile: Tile): Tile

    suspend fun insertTiles(tiles: List<Tile>): List<Tile>

    suspend fun updateTile(tile: Tile)

    suspend fun updateTiles(tiles: List<Tile>)

    suspend fun deleteTile(id: Long)

    suspend fun deleteTiles(tiles: List<Tile>)

    suspend fun updatePayloadAndGetTilesToNotify(
        subscribeTopic: String,
        payload: String
    ): List<Tile>

    suspend fun getDashboardTiles(dashboardId: Long): List<Tile>

    fun observeTile(id: Long): Flow<Tile>

    fun observeTopicUpdates(): Flow<Set<String>>
}