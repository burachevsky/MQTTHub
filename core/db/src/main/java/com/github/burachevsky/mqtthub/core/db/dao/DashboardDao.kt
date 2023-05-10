package com.github.burachevsky.mqtthub.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.github.burachevsky.mqtthub.core.db.entity.DashboardEntity
import com.github.burachevsky.mqtthub.core.db.entity.DashboardWithTiles
import kotlinx.coroutines.flow.Flow

@Dao
interface DashboardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dashboard: DashboardEntity): Long

    @Update
    suspend fun update(dashboard: DashboardEntity)

    @Query("UPDATE dashboards SET name = :name WHERE id = :dashboardId")
    suspend fun updateDashboardName(dashboardId: Long, name: String)

    @Query("DELETE FROM dashboards WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM dashboards WHERE id = :id")
    fun observe(id: Long): Flow<DashboardEntity?>

    @Query("SELECT * FROM dashboards ORDER BY id DESC")
    fun observeAll(): Flow<List<DashboardEntity>>

    @Transaction
    @Query("SELECT * FROM dashboards WHERE id = :id")
    suspend fun getDashboardWithTiles(id: Long): DashboardWithTiles
}