package com.github.burachevsky.mqtthub.feature.home

import androidx.navigation.NavController
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.feature.home.addtile.button.AddButtonTileFragmentArgs
import com.github.burachevsky.mqtthub.feature.home.addtile.switchh.AddSwitchFragmentArgs
import com.github.burachevsky.mqtthub.feature.home.addtile.text.AddTextTileFragmentArgs
import com.github.burachevsky.mqtthub.feature.home.publishtext.PublishTextDialogFragmentArgs

class HomeNavigator(navController: NavController) : Navigator(navController) {

    fun navigateAddTextTile(brokerId: Long, tileId: Long = 0, dashboardPosition: Int) =
        navController.navigate(
            R.id.navigateAddTextTile,
            AddTextTileFragmentArgs(brokerId, tileId, dashboardPosition).toBundle()
        )

    fun navigateAddButtonTile(brokerId: Long, tileId: Long = 0, dashboardPosition: Int) =
        navController.navigate(
            R.id.navigateAddButtonTile,
            AddButtonTileFragmentArgs(brokerId, tileId, dashboardPosition).toBundle()
        )

    fun navigateAddSwitch(brokerId: Long, tileId: Long = 0, dashboardPosition: Int) =
        navController.navigate(
            R.id.navigateAddSwitch,
            AddSwitchFragmentArgs(brokerId, tileId, dashboardPosition).toBundle()
        )

    fun navigateSelectTileType() = navController.navigate(R.id.navigateSelectTileType)

    fun navigatePublishTextDialog(tileId: Long, tileName: String) = navController.navigate(
        R.id.navigatePublishText,
        PublishTextDialogFragmentArgs(tileId, tileName).toBundle()
    )

    fun navigateAddBroker() = navController.navigate(R.id.navigateAddBroker)

    fun navigateEditBrokers() = navController.navigate(R.id.navigateBrokers)
}