package com.github.burachevsky.mqtthub.feature.home

import androidx.navigation.NavController
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.feature.home.addtile.text.AddTextTileFragmentArgs

class HomeNavigator(navController: NavController) : Navigator(navController) {

    fun navigateAddTextTile(brokerId: Long) = navController.navigate(
        R.id.navigateAddTextTile,
        AddTextTileFragmentArgs(brokerId).toBundle()
    )
}