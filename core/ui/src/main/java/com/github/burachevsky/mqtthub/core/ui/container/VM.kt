package com.github.burachevsky.mqtthub.core.ui.container

import com.github.burachevsky.mqtthub.core.ui.navigation.Navigator

interface VM<N : Navigator> {
    val container: ViewModelContainer<N>
}