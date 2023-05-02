package com.github.burachevsky.mqtthub.feature.home

import androidx.navigation.NavController
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.common.text.ParcelableTxt
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of
import com.github.burachevsky.mqtthub.feature.addtile.AddTileArgs
import com.github.burachevsky.mqtthub.feature.dashboards.DashboardsFragmentArgs
import com.github.burachevsky.mqtthub.feature.entertext.EnterTextDialogFragmentArgs
import com.github.burachevsky.mqtthub.feature.selector.SelectorConfig
import com.github.burachevsky.mqtthub.feature.selector.SelectorDialogFragmentArgs
import com.github.burachevsky.mqtthub.feature.selector.SelectorItem
import com.github.burachevsky.mqtthub.feature.tiledetails.text.TextTileDetailsFragmentArgs

class HomeNavigator(navController: NavController) : Navigator(navController) {

    fun navigateAddTextTile(dashboardId: Long, tileId: Long = 0, dashboardPosition: Int) =
        navController.navigate(
            R.id.navigateAddTextTile,
            AddTileArgs(dashboardId, tileId, dashboardPosition).toBundle()
        )

    fun navigateAddButtonTile(dashboardId: Long, tileId: Long = 0, dashboardPosition: Int) =
        navController.navigate(
            R.id.navigateAddButtonTile,
            AddTileArgs(dashboardId, tileId, dashboardPosition).toBundle()
        )

    fun navigateAddSwitch(dashboardId: Long, tileId: Long = 0, dashboardPosition: Int) =
        navController.navigate(
            R.id.navigateAddSwitch,
            AddTileArgs(dashboardId, tileId, dashboardPosition).toBundle()
        )

    fun navigateAddChart(dashboardId: Long, tileId: Long = 0, dashboardPosition: Int) =
        navController.navigate(
            R.id.navigateAddChart,
            AddTileArgs(dashboardId, tileId, dashboardPosition).toBundle()
        )

    fun navigateAddSlider(dashboardId: Long, tileId: Long = 0, dashboardPosition: Int) =
        navController.navigate(
            R.id.navigateAddSlider,
            AddTileArgs(dashboardId, tileId, dashboardPosition).toBundle()
        )

    fun navigateSelector(config: SelectorConfig) = navController.navigate(
        R.id.navigateSelector,
        SelectorDialogFragmentArgs(config).toBundle(),
    )

    fun navigateSelectTileType() = navController.navigate(
        R.id.navigateSelector,
        SelectorDialogFragmentArgs(
            SelectorConfig(
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
        ).toBundle()
    )

    fun navigateEnterText(
        actionId: Int,
        title: ParcelableTxt,
        initText: ParcelableTxt
    ) = navController.navigate(
        R.id.navigateEnterText,
        EnterTextDialogFragmentArgs(actionId, title, initText).toBundle()
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

    fun navigateSettings() = navController.navigate(R.id.navigateSettings)
}