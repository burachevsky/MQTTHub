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
    fun update(tile: Tile)

    @Query("DELETE FROM tiles WHERE id = :id")
    fun delete(id: Long)
}