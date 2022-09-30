package com.github.burachevsky.mqtthub.feature.brokers

import androidx.navigation.NavController
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.feature.addbroker.AddBrokerFragmentArgs
import com.github.burachevsky.mqtthub.feature.addbroker.BrokerInfo
import com.github.burachevsky.mqtthub.feature.home.HomeFragmentArgs

class BrokersNavigator(
    navController: NavController
): Navigator(navController) {

    fun navigateHome(id: Long) = navController.navigate(
        R.id.navigateHome,
        HomeFragmentArgs(id).toBundle(),
    )

    fun navigateAddBroker(
        brokerInfo: BrokerInfo? = null
    ) = navController.navigate(
        R.id.navigateAddBroker,
        AddBrokerFragmentArgs(brokerInfo).toBundle()
    )
}