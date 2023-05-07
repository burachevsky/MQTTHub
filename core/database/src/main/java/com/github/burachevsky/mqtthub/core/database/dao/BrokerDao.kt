package com.github.burachevsky.mqtthub.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.burachevsky.mqtthub.core.database.entity.BrokerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BrokerDao {

    @Query("SELECT * FROM brokers ORDER BY id DESC")
    suspend fun getAll(): List<BrokerEntity>

    @Query("SELECT * FROM brokers ORDER BY id DESC")
    fun observeBrokers(): Flow<List<BrokerEntity>>

    @Query("SELECT * FROM brokers WHERE id = :id")
    suspend fun getById(id: Long): BrokerEntity?

    @Query("SELECT * FROM brokers WHERE id = :id")
    fun observeBroker(id: Long): Flow<BrokerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(broker: BrokerEntity): Long

    @Update
    suspend fun update(broker: BrokerEntity)

    @Query("DELETE FROM brokers WHERE id = :id")
    suspend fun delete(id: Long)
}