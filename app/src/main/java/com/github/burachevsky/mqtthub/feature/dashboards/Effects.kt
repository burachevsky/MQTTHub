package com.github.burachevsky.mqtthub.feature.dashboards

import com.github.burachevsky.mqtthub.domain.eventbus.AppEvent
import com.github.burachevsky.mqtthub.data.entity.Dashboard

data class DashboardCreated(
    val dashboard: Dashboard
) : AppEvent

data class DashboardEdited(
    val dashboard: Dashboard
) : AppEvent

data class DashboardDeleted(
    val dashboardId: Long,
) : AppEvent