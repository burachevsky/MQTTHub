package com.github.burachevsky.mqtthub

import com.github.burachevsky.mqtthub.core.ui.container.SystemBarsSizeProvider

object AppSystemBarsSizeProvider : SystemBarsSizeProvider {
    var isInitialized = false

    override var statusBarHeight = 0
    override var navigationBarHeight = 0
}