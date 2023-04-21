package com.github.burachevsky.mqtthub.feature.home

import com.github.burachevsky.mqtthub.common.container.ViewModelContainer
import dagger.Module
import dagger.Provides

@Module
class HomeModule(private val fragment: HomeFragment) {

    @Provides
    fun provideVMContainer(): ViewModelContainer<HomeNavigator> {
        return fragment.viewModel.container
    }
}