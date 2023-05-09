package com.github.burachevsky.mqtthub.core.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tiles",
    foreignKeys = [
        ForeignKey(
            entity = DashboardEntity::class,
            parentColumns = ["id"],
            childColumns = ["dashboard_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TileEntity(
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
    val type: String,

    @ColumnInfo(name = "last_payload")
    val payload: String,

    @ColumnInfo(name = "notify_payload_update")
    val notifyPayloadUpdate: Boolean,

    @ColumnInfo(name = "state_list")
    val stateList: String,

    @ColumnInfo(name = "dashboard_id")
    val dashboardId: Long,

    @ColumnInfo(name = "dashboard_position")
    val dashboardPosition: Int,

    @ColumnInfo(name = "style_id")
    val styleId: Int,

    @ColumnInfo(name = "size_id")
    val sizeId: Int,

    @ColumnInfo(name = "is_full_span")
    val isFullSpan: Boolean,
)
