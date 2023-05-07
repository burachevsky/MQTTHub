package com.github.burachevsky.mqtthub.feature.home.drawer

import com.github.burachevsky.mqtthub.core.database.entity.dashboard.Dashboard
import com.github.burachevsky.mqtthub.core.eventbus.AppEvent

data class DashboardImported(val dashboard: Dashboard) : AppEvent