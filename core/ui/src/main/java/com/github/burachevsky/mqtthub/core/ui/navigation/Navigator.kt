package com.github.burachevsky.mqtthub.core.ui.navigation

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.github.burachevsky.mqtthub.core.ui.constant.NavDestination
import com.github.burachevsky.mqtthub.core.ui.container.NavDestinationMapper

open class Navigator(
    private val navController: NavController,
    private val action: NavDestinationMapper,
) {

    fun navigate(
        destination: NavDestination,
        args: Bundle? = null,
        options: NavOptions? = null,
        extras: Navigator.Extras? = null
    ) {
        navController.navigate(action.map(destination), args, options, extras)
    }

    fun back() {
        navController.popBackStack()
    }
}