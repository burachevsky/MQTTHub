package com.github.burachevsky.mqtthub.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class BrokerWithTiles(
    @Embedded
    val broker: Broker,

    @Relation(
        parentColumn = "id",
        entityColumn = "broker_id"
    )
    val tiles: List<Tile> = emptyList()
)
