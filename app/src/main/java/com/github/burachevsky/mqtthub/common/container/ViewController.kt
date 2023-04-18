package com.github.burachevsky.mqtthub.common.container

import androidx.lifecycle.LifecycleOwner

interface ViewController<VM : com.github.burachevsky.mqtthub.common.container.VM<*>> : LifecycleOwner {
    val container: ViewContainer
    val viewModel: VM
}