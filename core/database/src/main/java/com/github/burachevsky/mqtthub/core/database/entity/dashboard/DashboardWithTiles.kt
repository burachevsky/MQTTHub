package com.github.burachevsky.mqtthub.core.database.entity.dashboard

import androidx.room.Embedded
import androidx.room.Relation
import com.github.burachevsky.mqtthub.core.database.entity.tile.Tile
import com.google.gson.annotations.SerializedName

data class DashboardWithTiles(
    @Embedded
    @SerializedName("dashboard")
    val dashboard: Dashboard,

    @Relation(
        parentColumn = "id",
        entityColumn = "dashboard_id"
    )
    @SerializedName("tiles")
    val tiles: List<Tile> = emptyList()
)
