package com.github.burachevsky.mqtthub.common.container

interface DependableOnStatusBarHeight {

    fun fitSystemBars(statusBarHeight: Int, navigationBarHeight: Int)
}