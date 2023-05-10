package com.github.burachevsky.mqtthub

import com.github.burachevsky.mqtthub.core.ui.constant.NavDestination
import com.github.burachevsky.mqtthub.core.ui.container.NavDestinationMapper

object AppNavDestinationMapper : NavDestinationMapper {

    override fun map(destination: NavDestination): Int {
        return when (destination) {
            NavDestination.EnterText -> R.id.navigateEnterText
            NavDestination.TextTileDetails -> R.id.navigateTextTileDetails
            NavDestination.AddTextTile -> R.id.navigateAddTextTile
            NavDestination.AddButtonTile -> R.id.navigateAddButtonTile
            NavDestination.AddSwitchTile -> R.id.navigateAddSwitchTile
            NavDestination.AddChartTile -> R.id.navigateAddChartTile
            NavDestination.AddSliderTile -> R.id.navigateAddSliderTile
            NavDestination.Dashboards -> R.id.navigateDashboards
            NavDestination.AddBroker -> R.id.navigateAddBroker
            NavDestination.Brokers -> R.id.navigateBrokers
            NavDestination.Selector -> R.id.navigateSelector
            NavDestination.Settings -> R.id.navigateSettings
            NavDestination.HelpAndFeedback -> R.id.navigateHelpAndFeedback
        }
    }
}
