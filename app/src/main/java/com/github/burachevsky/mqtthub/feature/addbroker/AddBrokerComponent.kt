package com.github.burachevsky.mqtthub.feature.addbroker

import com.github.burachevsky.mqtthub.di.FragmentScope
import dagger.Subcomponent

@FragmentScope
@Subcomponent(modules = [AddBrokerModule::class])
interface AddBrokerComponent {

    fun inject(fragment: AddBrokerFragment)
}