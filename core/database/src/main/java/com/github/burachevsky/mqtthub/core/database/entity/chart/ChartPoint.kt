package com.github.burachevsky.mqtthub.core.database.entity.chart

import com.google.gson.annotations.SerializedName

data class ChartPoint(
    @SerializedName("x")
    val x: String,

    @SerializedName("y")
    val y: Float,
)