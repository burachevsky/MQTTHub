package com.github.burachevsky.mqtthub.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.github.burachevsky.mqtthub.core.database.entity.dashboard.Dashboard
import com.github.burachevsky.mqtthub.core.database.entity.dashboard.DashboardWithTiles
import kotlinx.coroutines.flow.Flow

@Dao
interface DashboardDao {

    @Query("SELECT * FROM dashboards ORDER BY id DESC")
    suspend fun getAll(): List<Dashboard>

    @Query("SELECT * FROM dashboards ORDER BY id DESC")
    fun observeDashboards(): Flow<List<Dashboard>>

    @Query("UPDATE dashboards SET name = :name WHERE id = :dashboardId")
    suspend fun updateDashboardName(dashboardId: Long, name: String)

    @Query("SELECT * FROM dashboards WHERE id = :id")
    fun observeDashboard(id: Long): Flow<Dashboard>

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