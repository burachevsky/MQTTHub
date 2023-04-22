package com.github.burachevsky.mqtthub.feature.home

import androidx.navigation.NavController
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of
import com.github.burachevsky.mqtthub.feature.dashboards.DashboardsFragmentArgs
import com.github.burachevsky.mqtthub.feature.addtile.button.AddButtonTileFragmentArgs
import com.github.burachevsky.mqtthub.feature.addtile.chart.AddChartTileFragmentArgs
import com.github.burachevsky.mqtthub.feature.addtile.switchh.AddSwitchFragmentArgs
import com.github.burachevsky.mqtthub.feature.addtile.text.AddTextTileFragmentArgs
import com.github.burachevsky.mqtthub.feature.publishtext.PublishTextDialogFragmentArgs
import com.github.burachevsky.mqtthub.feature.selector.SelectorConfig
import com.github.burachevsky.mqtthub.feature.selector.SelectorDialogFragmentArgs
import com.github.burachevsky.mqtthub.feature.selector.SelectorItem
import com.github.burachevsky.mqtthub.feature.tiledetails.text.TextTileDetailsFragmentArgs

class HomeNavigator(navController: NavController) : Navigator(navController) {

    fun navigateAddTextTile(dashboardId: Long, tileId: Long = 0, dashboardPosition: Int) =
        navController.navigate(
            R.id.navigateAddTextTile,
            AddTextTileFragmentArgs(dashboardId, tileId, dashboardPosition).toBundle()
        )

    fun navigateAddButtonTile(dashboardId: Long, tileId: Long = 0, dashboardPosition: Int) =
        navController.navigate(
            R.id.navigateAddButtonTile,
            AddButtonTileFragmentArgs(dashboardId, tileId, dashboardPosition).toBundle()
        )

    fun navigateAddSwitch(dashboardId: Long, tileId: Long = 0, dashboardPosition: Int) =
        navController.navigate(
            R.id.navigateAddSwitch,
            AddSwitchFragmentArgs(dashboardId, tileId, dashboardPosition).toBundle()
        )

    fun navigateAddChart(dashboardId: Long, tileId: Long = 0, dashboardPosition: Int) =
        navController.navigate(
            R.id.navigateAddChart,
            AddChartTileFragmentArgs(dashboardId, tileId, dashboardPosition).toBundle()
        )

    fun navigateSelectTileType() = navController.navigate(
        R.id.navigateSelector,
        SelectorDialogFragmentArgs(
            SelectorConfig(
                title = Txt.of(R.string.select_tile_type),
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
                    )
                )
            )
        ).toBundle()
    )

    fun navigatePublishTextDialog(tileId: Long, tileName: String) = navController.navigate(
        R.id.navigatePublishText,
        PublishTextDialogFragmentArgs(tileId, tileName).toBundle()
    )

    fun navigateAddBroker() = navController.navigate(R.id.navigateAddBroker)

    fun navigateEditBrokers() = navController.navigate(R.id.navigateBrokers)

    fun navigateEditDashboards(addNew: Boolean = false) = navController.navigate(
        R.id.navigateDashboards,
        DashboardsFragmentArgs(addNew).toBundle()
    )

    fun navigateTextTileDetails(tileId: Long) = navController.navigate(
        R.id.navigateTextTileDetails,
        TextTileDetailsFragmentArgs(tileId).toBundle()
    )
}