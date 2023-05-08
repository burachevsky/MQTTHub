package com.github.burachevsky.mqtthub.core.data.mapper

import android.util.Log
import com.github.burachevsky.mqtthub.core.common.Converter
import com.github.burachevsky.mqtthub.core.database.entity.TileEntity
import com.github.burachevsky.mqtthub.core.model.ChartPayload
import com.github.burachevsky.mqtthub.core.model.Payload
import com.github.burachevsky.mqtthub.core.model.Tile

fun Tile.asEntity(): TileEntity {
    return TileEntity(
        id = id,
        name = name,
        subscribeTopic = subscribeTopic,
        publishTopic = publishTopic,
        qos = qos,
        retained = retained,
        type = type.name,
        payload = payload.asEntity(),
        notifyPayloadUpdate = notifyPayloadUpdate,
        stateList = Converter.toJson(stateList),
        dashboardId = dashboardId,
        dashboardPosition = dashboardPosition,
        styleId = design.styleId,
        sizeId = design.sizeId,
        isFullSpan = design.isFullSpan,
    )
}

fun Payload.asEntity(): String {
    return when (this) {
        is ChartPayload -> Converter.toJson(this)
        else -> stringValue
    }
}

fun TileEntity.asModel(): Tile {
    val tileType = Tile.Type.valueOf(type)

    val stateList = stateList.asStateListModel()
    val payload = payload.asPayloadModel(tileType)

    Log.d("TileMapperDebug", "$payload")

    return Tile(
        id = id,
        name = name,
        subscribeTopic = subscribeTopic,
        publishTopic = publishTopic,
        qos = qos,
        retained = retained,
        type = tileType,
        payload = payload,
        notifyPayloadUpdate = notifyPayloadUpdate,
        stateList = stateList,
        dashboardId = dashboardId,
        dashboardPosition = dashboardPosition,
        design = Tile.Design(
            styleId = styleId,
            sizeId = sizeId,
            isFullSpan = isFullSpan
        ),
    )
}

fun String.asPayloadModel(type: Tile.Type): Payload {
    return Payload.map(this, type)
}

fun String.asStateListModel(): List<Tile.State> {
    return try {
        Converter.fromJson(this)
    } catch (e: Throwable) {
        emptyList()
    }
}