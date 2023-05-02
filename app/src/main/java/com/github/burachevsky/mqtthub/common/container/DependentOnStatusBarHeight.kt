package com.github.burachevsky.mqtthub.common.container

interface DependentOnStatusBarHeight {

    fun fitSystemBars(statusBarHeight: Int, navigationBarHeight: Int)
}