package com.github.burachevsky.mqtthub.data.entity.chart

import com.google.gson.annotations.SerializedName

data class ChartPayload(
    @SerializedName("x_title")
    val xTitle: String,
    
    @SerializedName("y_title")
    val yTitle: String,
    
    @SerializedName("data")
    val data: List<ChartPoint>,
)
