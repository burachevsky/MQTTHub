package com.github.burachevsky.mqtthub.core.ui.container

import com.github.burachevsky.mqtthub.core.ui.constant.NavDestination

fun interface NavDestinationMapper {

    fun map(destination: NavDestination): Int

    interface Provider {
        fun provideNavDestinationMapper(): NavDestinationMapper
    }
}