package com.github.burachevsky.mqtthub.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.github.burachevsky.mqtthub.data.entity.Dashboard
import com.github.burachevsky.mqtthub.data.entity.DashboardWithTiles

@Dao
interface DashboardDao {

    @Query("SELECT * FROM dashboards ORDER BY id DESC")
    suspend fun getAll(): List<Dashboard>

    @Query("SELECT * FROM dashboards WHERE id = :id")
    suspend fun getById(id: Long): Dashboard

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dashboard: Dashboard): Long

    @Update
    suspend fun update(dashboard: Dashboard)

    @Query("DELETE FROM dashboards WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT COUNT(*) FROM dashboards")
    suspend fun count(): Int

    @Transaction
    @Query("SELECT * FROM dashboards WHERE id = :id")
    suspend fun getDashboardWithTiles(id: Long): DashboardWithTiles
}