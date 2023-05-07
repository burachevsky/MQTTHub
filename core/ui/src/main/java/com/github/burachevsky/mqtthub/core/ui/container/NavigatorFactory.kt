package com.github.burachevsky.mqtthub.core.ui.container

import androidx.navigation.NavController
import com.github.burachevsky.mqtthub.core.ui.navigation.Navigator

fun interface NavigatorFactory {
    fun createNavigator(
        navController: NavController,
        actionProvider: NavDestinationMapper,
    ): Navigator
}