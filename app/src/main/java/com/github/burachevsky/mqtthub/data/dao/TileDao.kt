package com.github.burachevsky.mqtthub.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.burachevsky.mqtthub.data.entity.Tile
import kotlinx.coroutines.flow.Flow

@Dao
interface TileDao {

    @Insert
    suspend fun insert(tile: Tile): Long

    @Update
    suspend fun update(tile: Tile)

    @Update
    suspend fun update(tiles: List<Tile>)

    @Query("DELETE FROM tiles WHERE id = :id")
    suspend fun delete(id: Long)

    @Delete
    suspend fun delete(ids: List<Tile>)

    @Query(
        """
        UPDATE tiles 
        SET last_payload = :payload
        WHERE subscribe_topic = :subscribeTopic"""
    )
    suspend fun updatePayload(subscribeTopic: String, payload: String)

    @Query("SELECT * FROM tiles WHERE id = :id")
    suspend fun getById(id: Long): Tile

    @Query("SELECT * FROM tiles WHERE dashboard_id = :dashboardId ORDER BY dashboard_position")
    suspend fun getDashboardTiles(dashboardId: Long): List<Tile>

    @Query("SELECT * FROM tiles")
    suspend fun getAllTiles(): List<Tile>

    @Query("SELECT * FROM tiles WHERE id = :id")
    fun observeTile(id: Long): Flow<Tile>
}