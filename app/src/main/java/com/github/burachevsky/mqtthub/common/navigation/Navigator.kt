package com.github.burachevsky.mqtthub.common.navigation

import androidx.navigation.NavController

open class Navigator(
    protected val navController: NavController
) {

    fun back() {
        navController.popBackStack()
    }
}