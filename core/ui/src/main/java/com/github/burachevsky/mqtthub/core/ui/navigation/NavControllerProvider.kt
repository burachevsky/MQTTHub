package com.github.burachevsky.mqtthub.core.ui.navigation

import androidx.navigation.NavController

interface NavControllerProvider {

    fun provideNavController(): NavController
}