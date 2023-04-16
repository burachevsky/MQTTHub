package com.github.burachevsky.mqtthub.feature.dashboards

import com.github.burachevsky.mqtthub.common.effect.UIEffect
import com.github.burachevsky.mqtthub.data.entity.Dashboard

data class DashboardCreated(
    val dashboard: Dashboard
) : UIEffect

data class DashboardEdited(
    val dashboard: Dashboard
) : UIEffect

data class DashboardDeleted(
    val dashboardId: Long,
) : UIEffect