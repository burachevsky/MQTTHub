package com.github.burachevsky.mqtthub.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class DashboardWithTiles(
    @Embedded
    val dashboard: Dashboard,

    @Relation(
        parentColumn = "id",
        entityColumn = "dashboard_id"
    )
    val tiles: List<Tile> = emptyList()
)
