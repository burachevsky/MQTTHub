package com.github.burachevsky.mqtthub.core.ui.container

import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

interface ViewController<VM : com.github.burachevsky.mqtthub.core.ui.container.VM<*>>
    : LifecycleOwner {

    val binding: ViewBinding
    val viewModel: VM
    val container: ViewContainer
}