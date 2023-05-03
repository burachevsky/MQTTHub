package com.github.burachevsky.mqtthub.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.github.burachevsky.mqtthub.data.entity.SimpleTile
import com.github.burachevsky.mqtthub.data.entity.Tile
import kotlinx.coroutines.flow.Flow

@Dao
interface TileDao {

    @Insert
    suspend fun insert(tile: Tile): Long

    @Update
    suspend fun update(tile: Tile)

    @Update
    fun update(tiles: List<Tile>)

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

    @Query(
        """SELECT id, name, last_payload 
        FROM tiles 
        WHERE notify_payload_update = :notify
        AND subscribe_topic = :subscribeTopic """
    )
    suspend fun getTilesToNotify(
        subscribeTopic: String,
        notify: Boolean = true
    ): List<SimpleTile>

    @Transaction
    suspend fun updatePayloadAndGetTilesToNotify(
        subscribeTopic: String,
        payload: String,
    ): List<SimpleTile> {
        updatePayload(subscribeTopic, payload)
        return getTilesToNotify(subscribeTopic)
    }

    @Query("SELECT * FROM tiles WHERE id = :id")
    suspend fun getById(id: Long): Tile

    @Query("SELECT * FROM tiles WHERE id = :id")
    fun observeTile(id: Long): Flow<Tile>
}