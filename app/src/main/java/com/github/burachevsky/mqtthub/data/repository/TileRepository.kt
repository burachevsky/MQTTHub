package com.github.burachevsky.mqtthub.data.repository

import com.github.burachevsky.mqtthub.data.entity.SimpleTile
import com.github.burachevsky.mqtthub.data.entity.Tile
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
    ): List<SimpleTile>

    suspend fun getTile(id: Long): Tile

    fun observeTile(id: Long): Flow<Tile>
}