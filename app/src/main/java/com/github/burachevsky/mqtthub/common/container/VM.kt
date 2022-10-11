package com.github.burachevsky.mqtthub.common.container

import com.github.burachevsky.mqtthub.common.navigation.Navigator

interface VM<N : Navigator> {
    val container: ViewModelContainer<N>
}