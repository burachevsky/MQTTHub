package com.github.burachevsky.mqtthub.core.database.entity.broker

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "brokers"
)
data class Broker(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "port")
    val port: String,

    @ColumnInfo(name = "clientId")
    val clientId: String,
)
