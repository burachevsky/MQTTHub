package com.github.burachevsky.mqtthub.feature.home.drawer

import com.github.burachevsky.mqtthub.core.eventbus.AppEvent
import com.github.burachevsky.mqtthub.core.model.Dashboard

data class DashboardImported(val dashboard: Dashboard) : AppEvent