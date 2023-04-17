package com.github.burachevsky.mqtthub.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "current_ids"
)
data class CurrentIds(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "current_broker_id")
    val currentBrokerId: Long? = null,

    @ColumnInfo(name ="current_dashboard_id")
    val currentDashboardId: Long? = null,
)
