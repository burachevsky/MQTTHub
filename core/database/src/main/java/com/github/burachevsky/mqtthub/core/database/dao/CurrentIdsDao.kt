package com.github.burachevsky.mqtthub.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.burachevsky.mqtthub.core.database.entity.CurrentIdsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentIdsDao {

    @Query("SELECT current_broker_id FROM current_ids WHERE id = 0")
    suspend fun getCurrentBrokerId(): Long?

    @Query("SELECT current_dashboard_id FROM current_ids WHERE id = 0")
    suspend fun getCurrentDashboardId(): Long?

    @Query("SELECT * FROM current_ids WHERE id = 0")
    suspend fun getCurrentIds(): CurrentIdsEntity

    @Query("SELECT * FROM current_ids WHERE id = 0")
    fun observeCurrentIds(): Flow<CurrentIdsEntity?>

    @Query("SELECT current_dashboard_id FROM current_ids WHERE id = 0")
    fun observeCurrentDashboardId(): Flow<Long?>

    @Query("SELECT current_broker_id FROM current_ids WHERE id = 0")
    fun observeCurrentBrokerId(): Flow<Long?>

    @Query("UPDATE current_ids SET current_broker_id = :brokerId WHERE id = 0")
    suspend fun updateCurrentBrokerId(brokerId: Long?)

    @Query("UPDATE current_ids SET current_dashboard_id = :dashboardId WHERE id = 0")
    suspend fun updateCurrentDashboardId(dashboardId: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun init(currentIds: CurrentIdsEntity = CurrentIdsEntity())
}