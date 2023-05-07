package com.github.burachevsky.mqtthub.core.data.mapper

import com.github.burachevsky.mqtthub.core.database.entity.CurrentIdsEntity
import com.github.burachevsky.mqtthub.core.model.CurrentIds

fun CurrentIds.asEntity(): CurrentIdsEntity {
    return CurrentIdsEntity(
        currentBrokerId = currentBrokerId,
        currentDashboardId = currentDashboardId,
    )
}

fun CurrentIdsEntity.asModel(): CurrentIds {
    return CurrentIds(
        currentBrokerId = currentBrokerId,
        currentDashboardId = currentDashboardId,
    )
}