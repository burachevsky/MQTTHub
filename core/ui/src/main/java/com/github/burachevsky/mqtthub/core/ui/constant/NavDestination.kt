package com.github.burachevsky.mqtthub.core.ui.constant

sealed class NavDestination {
    object EnterText : NavDestination()
    object TextTileDetails : NavDestination()
    object AddTextTile : NavDestination()
    object AddButtonTile : NavDestination()
    object AddSwitchTile : NavDestination()
    object AddChartTile : NavDestination()
    object AddSliderTile : NavDestination()
    object Dashboards : NavDestination()
    object AddBroker : NavDestination()
    object Brokers : NavDestination()
    object Selector : NavDestination()
    object Settings : NavDestination()
}