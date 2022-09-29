package com.github.burachevsky.mqtthub.common.container

import androidx.navigation.NavController
import com.github.burachevsky.mqtthub.common.navigation.Navigator

fun interface NavigatorFactory {
    fun createNavigator(navController: NavController): Navigator
}