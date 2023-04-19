package com.github.burachevsky.mqtthub.feature.home

import androidx.navigation.NavController
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.feature.dashboards.DashboardsFragmentArgs
import com.github.burachevsky.mqtthub.feature.home.addtile.button.AddButtonTileFragmentArgs
import com.github.burachevsky.mqtthub.feature.home.addtile.switchh.AddSwitchFragmentArgs
import com.github.burachevsky.mqtthub.feature.home.addtile.text.AddTextTileFragmentArgs
import com.github.burachevsky.mqtthub.feature.home.publishtext.PublishTextDialogFragmentArgs
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

    fun navigateSelectTileType() = navController.navigate(R.id.navigateSelectTileType)

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