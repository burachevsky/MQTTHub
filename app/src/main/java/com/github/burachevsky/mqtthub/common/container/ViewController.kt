package com.github.burachevsky.mqtthub.common.container

import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

interface ViewController<VM : com.github.burachevsky.mqtthub.common.container.VM<*>>
    : LifecycleOwner {

    val binding: ViewBinding
    val viewModel: VM
    val container: ViewContainer
}