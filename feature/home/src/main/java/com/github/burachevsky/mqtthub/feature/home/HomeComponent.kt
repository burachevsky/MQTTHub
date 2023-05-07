package com.github.burachevsky.mqtthub.feature.home

import com.github.burachevsky.mqtthub.core.ui.di.FragmentScope
import dagger.Subcomponent

@FragmentScope
@Subcomponent(modules = [HomeModule::class])
interface HomeComponent {

    fun inject(fragment: HomeFragment)

    interface Provider {
        fun homeComponent(module: HomeModule): HomeComponent
    }
}
