package com.github.burachevsky.mqtthub.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.burachevsky.mqtthub.core.db.entity.BrokerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BrokerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(broker: BrokerEntity): Long

    @Update
    suspend fun update(broker: BrokerEntity)

    @Query("DELETE FROM brokers WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM brokers ORDER BY id DESC")
    fun observeAll(): Flow<List<BrokerEntity>>

    @Query("SELECT * FROM brokers WHERE id = :id")
    fun observe(id: Long): Flow<BrokerEntity>
}