package com.github.burachevsky.mqtthub.core.model

import com.google.gson.annotations.SerializedName
import java.util.Objects

data class ChartPayload(
    @SerializedName("x_title")
    val xTitle: String,

    @SerializedName("y_title")
    val yTitle: String,

    @SerializedName("data")
    val data: List<ChartPoint>,
) : Payload {

    override val stringValue = ""

    override fun hashCode(): Int {
        return Objects.hash(xTitle, yTitle, data)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChartPayload

        if (xTitle != other.xTitle) return false
        if (yTitle != other.yTitle) return false
        if (data != other.data) return false

        return true
    }
}

data class ChartPoint(
    @SerializedName("x")
    val x: String,

    @SerializedName("y")
    val y: Float,
)