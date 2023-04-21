package com.github.burachevsky.mqtthub.feature.brokers

import androidx.navigation.NavController
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.feature.addbroker.AddBrokerFragmentArgs

class BrokersNavigator(
    navController: NavController
): Navigator(navController) {

    fun navigateHome() = navController.popBackStack()

    fun navigateAddBroker(brokerId: Long = 0) = navController.navigate(
        R.id.navigateAddBroker,
        AddBrokerFragmentArgs(brokerId).toBundle()
    )
}