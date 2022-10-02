package com.github.burachevsky.mqtthub.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.burachevsky.mqtthub.data.entity.Tile

@Dao
interface TileDao {

    @Query("SELECT * FROM tiles WHERE id = :brokerId")
    suspend fun getAllBrokerTiles(brokerId: Long): List<Tile>

    @Insert
    suspend fun insert(tile: Tile): Long

    @Update
    suspend fun update(tile: Tile)

    @Query("DELETE FROM tiles WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("""
        UPDATE tiles 
        SET last_payload = :payload
        WHERE broker_id = :brokerId AND subscribe_topic = :subscribeTopic"""
    )
    suspend fun updatePayload(brokerId: Long, subscribeTopic: String, payload: String)

    @Query("SELECT * FROM tiles WHERE id = :id")
    suspend fun getById(id: Long): Tile
}