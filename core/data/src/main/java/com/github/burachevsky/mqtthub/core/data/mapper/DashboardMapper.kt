package com.github.burachevsky.mqtthub.core.data.mapper

import com.github.burachevsky.mqtthub.core.database.entity.DashboardEntity
import com.github.burachevsky.mqtthub.core.model.Dashboard

fun Dashboard.asEntity(): DashboardEntity {
    return DashboardEntity(
        id = id,
        name = name,
        position = position,
    )
}

fun DashboardEntity.asModel(): Dashboard {
    return Dashboard(
        id = id,
        name = name,
        position = position,
    )
}