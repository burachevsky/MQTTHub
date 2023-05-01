package com.github.burachevsky.mqtthub.feature.homedrawer

import com.github.burachevsky.mqtthub.data.entity.Dashboard
import com.github.burachevsky.mqtthub.domain.eventbus.AppEvent

data class DashboardImported(val dashboard: Dashboard) : AppEvent