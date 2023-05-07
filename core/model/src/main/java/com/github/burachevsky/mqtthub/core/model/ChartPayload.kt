package com.github.burachevsky.mqtthub.core.model

import com.google.gson.annotations.SerializedName

data class ChartPayload(
    @SerializedName("x_title")
    val xTitle: String,

    @SerializedName("y_title")
    val yTitle: String,

    @SerializedName("data")
    val data: List<ChartPoint>,
) : Payload {

    override val stringValue = ""
}

data class ChartPoint(
    @SerializedName("x")
    val x: String,

    @SerializedName("y")
    val y: Float,
)