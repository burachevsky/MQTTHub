package com.github.burachevsky.mqtthub.feature.home

import com.github.burachevsky.mqtthub.data.entity.Broker
import com.github.burachevsky.mqtthub.data.entity.Dashboard
import com.github.burachevsky.mqtthub.domain.eventbus.AppEvent

object CloseHomeDrawer : AppEvent

object CloseHomeDrawerOrNavigateUp : AppEvent

data class BrokerChanged(val broker: Broker?) : AppEvent

data class DashboardChanged(val dashboard: Dashboard) : AppEvent

data class OpenTextTileDetails(
    val position: Int, val tileId: Long
) : AppEvent