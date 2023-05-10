package com.github.burachevsky.mqtthub.feature.home

import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.constant.NavArg
import com.github.burachevsky.mqtthub.core.ui.constant.NavDestination
import com.github.burachevsky.mqtthub.core.ui.container.NavDestinationMapper
import com.github.burachevsky.mqtthub.core.ui.dialog.entertext.EnterTextConfig
import com.github.burachevsky.mqtthub.core.ui.dialog.selector.SelectorConfig
import com.github.burachevsky.mqtthub.core.ui.dialog.selector.SelectorItem
import com.github.burachevsky.mqtthub.core.ui.navigation.Navigator
import com.github.burachevsky.mqtthub.core.ui.text.ParcelableTxt
import com.github.burachevsky.mqtthub.core.ui.text.Txt
import com.github.burachevsky.mqtthub.core.ui.text.of

class HomeNavigator(
    navController: NavController,
    action: NavDestinationMapper,
): Navigator(navController, action) {

    fun navigateAddTextTile(dashboardId: Long, tileId: Long = 0, dashboardPosition: Int) =
        navigate(
            NavDestination.AddTextTile,
            bundleOf(
                NavArg.DASHBOARD_ID to dashboardId,
                NavArg.TILE_ID to tileId,
                NavArg.DASHBOARD_POSITION to dashboardPosition
            )
        )

    fun navigateAddButtonTile(dashboardId: Long, tileId: Long = 0, dashboardPosition: Int) =
        navigate(
            NavDestination.AddButtonTile,
            bundleOf(
                NavArg.DASHBOARD_ID to dashboardId,
                NavArg.TILE_ID to tileId,
                NavArg.DASHBOARD_POSITION to dashboardPosition
            )
        )

    fun navigateAddSwitch(dashboardId: Long, tileId: Long = 0, dashboardPosition: Int) =
        navigate(
            NavDestination.AddSwitchTile,
            bundleOf(
                NavArg.DASHBOARD_ID to dashboardId,
                NavArg.TILE_ID to tileId,
                NavArg.DASHBOARD_POSITION to dashboardPosition
            )
        )

    fun navigateAddChart(dashboardId: Long, tileId: Long = 0, dashboardPosition: Int) =
        navigate(
            NavDestination.AddChartTile,
            bundleOf(
                NavArg.DASHBOARD_ID to dashboardId,
                NavArg.TILE_ID to tileId,
                NavArg.DASHBOARD_POSITION to dashboardPosition
            )
        )

    fun navigateAddSlider(dashboardId: Long, tileId: Long = 0, dashboardPosition: Int) =
        navigate(
            NavDestination.AddSliderTile,
            bundleOf(
                NavArg.DASHBOARD_ID to dashboardId,
                NavArg.TILE_ID to tileId,
                NavArg.DASHBOARD_POSITION to dashboardPosition
            )
        )

    fun navigateSelector(config: SelectorConfig) = navigate(
        NavDestination.Selector,
        bundleOf(
            NavArg.CONFIG to config
        )
    )

    fun navigateSelectTileType() = navigate(
        NavDestination.Selector,
        bundleOf(
            NavArg.CONFIG to SelectorConfig(
                title = Txt.of(R.string.add_tile),
                items = listOf(
                    SelectorItem(
                        id = TileTypeId.TEXT,
                        text = Txt.of(R.string.tile_type_text),
                        icon = R.drawable.ic_text_type,
                    ),
                    SelectorItem(
                        id = TileTypeId.BUTTON,
                        text = Txt.of(R.string.tile_type_button),
                        icon = R.drawable.ic_button_type,
                    ),
                    SelectorItem(
                        id = TileTypeId.SWITCH,
                        text = Txt.of(R.string.tile_type_switch),
                        icon = R.drawable.ic_switch_type,
                    ),
                    SelectorItem(
                        id = TileTypeId.CHART,
                        text = Txt.of(R.string.tile_type_chart),
                        icon = R.drawable.ic_chart_type,
                    ),
                    SelectorItem(
                        id = TileTypeId.SLIDER,
                        text = Txt.of(R.string.tile_type_slider),
                        icon = R.drawable.ic_slider_type,
                    ),
                )
            )
        )
    )

    fun navigateEnterText(
        actionId: Int,
        title: ParcelableTxt,
        initText: ParcelableTxt
    ) = navigate(
        NavDestination.EnterText,
        bundleOf(
            NavArg.CONFIG to EnterTextConfig(
                actionId = actionId,
                title = title,
                initText = initText,
            )
        )
    )

    fun navigateAddBroker() = navigate(NavDestination.AddBroker)

    fun navigateEditBrokers() = navigate(NavDestination.Brokers)

    fun navigateEditDashboards(addNew: Boolean = false) = navigate(
        NavDestination.Dashboards,
        bundleOf(
            NavArg.ADD_NEW to addNew
        )
    )

    fun navigateSettings() = navigate(NavDestination.Settings)

    fun navigateHelpAndFeedback() = navigate(NavDestination.HelpAndFeedback)
}