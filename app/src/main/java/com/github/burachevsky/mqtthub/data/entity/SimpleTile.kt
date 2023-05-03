package com.github.burachevsky.mqtthub.data.entity

import androidx.room.ColumnInfo

data class SimpleTile(
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "last_payload")
    val payload: String,
)
