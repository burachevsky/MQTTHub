package com.github.burachevsky.mqtthub.core.database.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.google.gson.annotations.SerializedName

data class DashboardWithTiles(
    @Embedded
    @SerializedName("dashboard")
    val dashboard: DashboardEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "dashboard_id"
    )
    @SerializedName("tiles")
    val tiles: List<TileEntity> = emptyList()
)
