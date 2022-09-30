package com.github.burachevsky.mqtthub.feature.home

import dagger.Module
import dagger.Provides

@Module
class HomeModule(fragment: HomeFragment) {
    private val args = HomeFragmentArgs.fromBundle(fragment.requireArguments())

    @Provides
    fun provideArgs(): HomeFragmentArgs {
        return args
    }
}