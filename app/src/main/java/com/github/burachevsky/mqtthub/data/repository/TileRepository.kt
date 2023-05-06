package com.github.burachevsky.mqtthub.data.repository

import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.data.entity.TopicUpdate
import kotlinx.coroutines.flow.Flow

interface TileRepository {

    suspend fun insertTile(tile: Tile): Tile

    suspend fun updateTile(tile: Tile)

    suspend fun updateTiles(tiles: List<Tile>)

    suspend fun deleteTile(id: Long)

    suspend fun deleteTiles(tiles: List<Tile>)

    suspend fun updatePayloadAndGetTilesToNotify(
        subscribeTopic: String,
        payload: String
    ): List<Tile>

    suspend fun getTile(id: Long): Tile

    suspend fun getDashboardTiles(dashboardId: Long): List<Tile>

    suspend fun getAllTiles(): List<Tile>

    fun observeTile(id: Long): Flow<Tile>

    fun observeTopicUpdates(): Flow<TopicUpdate>
}