package com.github.burachevsky.mqtthub.data.entity.chart

import com.google.gson.annotations.SerializedName

data class ChartPoint(
    @SerializedName("x")
    val x: String,

    @SerializedName("y")
    val y: Float,
)