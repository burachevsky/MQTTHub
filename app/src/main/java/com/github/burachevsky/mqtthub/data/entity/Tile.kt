package com.github.burachevsky.mqtthub.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tiles",
    foreignKeys = [
        ForeignKey(
            entity = Broker::class,
            parentColumns = ["id"],
            childColumns = ["broker_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Tile(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "subscribe_topic")
    val subscribeTopic: String,

    @ColumnInfo(name = "publish_topic")
    val publishTopic: String,

    @ColumnInfo(name = "qos")
    val qos: Int,

    @ColumnInfo(name = "retained")
    val retained: Boolean,

    @ColumnInfo(name = "type")
    val type: Type,

    @ColumnInfo(name = "last_payload")
    val payload: String = "",

    @ColumnInfo(name = "broker_id")
    val brokerId: Long,
) {

    enum class Type {
        TEXT, BUTTON
    }
}