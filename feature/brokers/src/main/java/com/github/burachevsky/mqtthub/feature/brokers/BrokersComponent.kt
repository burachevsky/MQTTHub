package com.github.burachevsky.mqtthub.feature.brokers

import com.github.burachevsky.mqtthub.core.ui.di.FragmentScope
import dagger.Subcomponent

@FragmentScope
@Subcomponent
interface BrokersComponent {

    fun inject(brokersFragment: BrokersFragment)

    interface Provider {
        fun brokersComponent(): BrokersComponent
    }
}