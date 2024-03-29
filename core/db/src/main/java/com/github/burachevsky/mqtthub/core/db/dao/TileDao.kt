package com.github.burachevsky.mqtthub.core.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.burachevsky.mqtthub.core.db.entity.TileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TileDao {

    @Insert
    suspend fun insert(tile: TileEntity): Long

    @Update
    suspend fun update(tile: TileEntity)

    @Update
    suspend fun update(tiles: List<TileEntity>)

    @Query("DELETE FROM tiles WHERE id = :id")
    suspend fun delete(id: Long)

    @Delete
    suspend fun delete(ids: List<TileEntity>)

    @Query("SELECT * FROM tiles")
    suspend fun getAll(): List<TileEntity>

    @Query(
        """
        UPDATE tiles 
        SET last_payload = :payload
        WHERE subscribe_topic = :subscribeTopic"""
    )
    suspend fun updatePayload(subscribeTopic: String, payload: String)

    @Query("SELECT * FROM tiles WHERE id = :id")
    fun observe(id: Long): Flow<TileEntity>
}