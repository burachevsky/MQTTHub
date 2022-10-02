package com.github.burachevsky.mqtthub.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.github.burachevsky.mqtthub.data.entity.Broker
import com.github.burachevsky.mqtthub.data.entity.BrokerWithTiles
import kotlinx.coroutines.flow.Flow

@Dao
interface BrokerDao {

    @Query("SELECT * FROM brokers")
    suspend fun getAll(): List<Broker>

    @Query("SELECT * FROM brokers WHERE id = :id")
    suspend fun getById(id: Long): Broker

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(broker: Broker): Long

    @Update
    suspend fun update(broker: Broker)

    @Query("DELETE FROM brokers WHERE id = :id")
    suspend fun delete(id: Long)

    @Transaction
    @Query("SELECT * FROM brokers WHERE id = :id")
    suspend fun getBrokerWithTiles(id: Long): BrokerWithTiles
}