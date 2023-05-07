package com.github.burachevsky.mqtthub.feature.brokers

import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.github.burachevsky.mqtthub.core.ui.constant.NavArg
import com.github.burachevsky.mqtthub.core.ui.constant.NavDestination
import com.github.burachevsky.mqtthub.core.ui.container.NavDestinationMapper
import com.github.burachevsky.mqtthub.core.ui.navigation.Navigator

class BrokersNavigator(
    navController: NavController,
    action: NavDestinationMapper,
): Navigator(navController, action) {

    fun navigateAddBroker(brokerId: Long = 0) = navigate(
        NavDestination.AddBroker,
        bundleOf(
            NavArg.BROKER_ID to brokerId
        )
    )
}