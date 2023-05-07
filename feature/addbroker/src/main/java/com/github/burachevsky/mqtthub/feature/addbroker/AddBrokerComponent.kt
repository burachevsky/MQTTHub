package com.github.burachevsky.mqtthub.feature.addbroker

import com.github.burachevsky.mqtthub.core.ui.di.FragmentScope
import dagger.Subcomponent

@FragmentScope
@Subcomponent(modules = [AddBrokerModule::class])
interface AddBrokerComponent {

    fun inject(fragment: AddBrokerFragment)

    interface Provider {
        fun addBrokerComponent(module: AddBrokerModule): AddBrokerComponent
    }
}