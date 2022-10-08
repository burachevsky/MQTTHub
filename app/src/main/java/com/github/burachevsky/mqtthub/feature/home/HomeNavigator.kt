package com.github.burachevsky.mqtthub.feature.home

import androidx.navigation.NavController
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.feature.home.addtile.button.AddButtonTileFragmentArgs
import com.github.burachevsky.mqtthub.feature.home.addtile.switchh.AddSwitchFragmentArgs
import com.github.burachevsky.mqtthub.feature.home.addtile.text.AddTextTileFragmentArgs

class HomeNavigator(navController: NavController) : Navigator(navController) {

    fun navigateAddTextTile(brokerId: Long, tileId: Long = 0) = navController.navigate(
        R.id.navigateAddTextTile,
        AddTextTileFragmentArgs(brokerId, tileId).toBundle()
    )

    fun navigateAddButtonTile(brokerId: Long, tileId: Long = 0) = navController.navigate(
        R.id.navigateAddButtonTile,
        AddButtonTileFragmentArgs(brokerId, tileId).toBundle()
    )

    fun navigateAddSwitch(brokerId: Long, tileId: Long = 0) = navController.navigate(
        R.id.navigateAddSwitch,
        AddSwitchFragmentArgs(brokerId, tileId).toBundle()
    )

    fun navigateSelectTileType() = navController.navigate(R.id.navigateSelectTileType)
}