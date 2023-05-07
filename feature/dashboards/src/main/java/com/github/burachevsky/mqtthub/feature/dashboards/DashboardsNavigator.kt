package com.github.burachevsky.mqtthub.feature.dashboards

import androidx.navigation.NavController
import com.github.burachevsky.mqtthub.core.ui.container.NavDestinationMapper
import com.github.burachevsky.mqtthub.core.ui.navigation.Navigator

class DashboardsNavigator(
    navController: NavController,
    action: NavDestinationMapper,
): Navigator(navController, action)