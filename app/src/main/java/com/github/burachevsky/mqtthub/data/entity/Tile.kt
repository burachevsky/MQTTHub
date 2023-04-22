package com.github.burachevsky.mqtthub.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.github.burachevsky.mqtthub.data.Converters
import com.github.burachevsky.mqtthub.data.entity.chart.ChartPayload
import com.google.gson.annotations.SerializedName
import timber.log.Timber

@Entity(
    tableName = "tiles",
    foreignKeys = [
        ForeignKey(
            entity = Dashboard::class,
            parentColumns = ["id"],
            childColumns = ["dashboard_id"],
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

    @ColumnInfo(name = "state_list")
    val stateList: List<State>,

    @ColumnInfo(name = "dashboard_id")
    val dashboardId: Long,

    @ColumnInfo(name = "dashboard_position")
    val dashboardPosition: Int = 0,

    @ColumnInfo(name = "design")
    val design: Design = Design()
) {

    @Ignore
    var chartPayload: ChartPayload? = null

    init {
        initPayload()
    }

    fun initPayload(): Tile {
        when (type) {
            Type.CHART -> try {
                chartPayload = Converters.fromJson<ChartPayload>(payload)
            } catch (e: Throwable) {
                Timber.e(e)
            }

            else -> {}
        }

        return this
    }

    enum class Type {
        TEXT, BUTTON, SWITCH, CHART
    }

    data class State(
        @SerializedName("id")
        val id: Int,

        @SerializedName("payload")
        val payload: String
    )

    data class Design(
        @SerializedName("styleId")
        val styleId: Int = 0,

        @SerializedName("sizeId")
        val sizeId: Int = 0,

        @SerializedName("isFullSpan")
        val isFullSpan: Boolean = false,
    )
}
