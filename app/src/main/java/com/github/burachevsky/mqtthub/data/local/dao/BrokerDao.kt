package com.github.burachevsky.mqtthub.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.burachevsky.mqtthub.data.local.entity.Broker

@Dao
interface BrokerDao {

    @Query("SELECT * FROM brokers")
    suspend fun getAllBrokers(): List<Broker>

    @Insert
    suspend fun insertBroker(broker: Broker): Long

    @Update
    suspend fun updateBroker(broker: Broker)

    @Query("DELETE FROM brokers WHERE id = :id")
    suspend fun deleteBroker(id: Long)
}