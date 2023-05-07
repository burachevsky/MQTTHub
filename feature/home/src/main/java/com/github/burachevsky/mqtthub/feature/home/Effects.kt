package com.github.burachevsky.mqtthub.feature.home

import com.github.burachevsky.mqtthub.core.eventbus.AppEvent

object CloseHomeDrawer : AppEvent

object CloseHomeDrawerOrNavigateUp : AppEvent

data class OpenTextTileDetails(val position: Int, val tileId: Long) : AppEvent

data class ExportDashboard(val fileName: String) : AppEvent

object ImportDashboard : AppEvent