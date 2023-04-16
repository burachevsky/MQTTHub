package com.github.burachevsky.mqtthub.feature.dashboards

import dagger.Module
import dagger.Provides

@Module
class DashboardsModule(fragment: DashboardsFragment) {
    private val args = DashboardsFragmentArgs.fromBundle(fragment.requireArguments())

    @Provides
    fun provideArgs(): DashboardsFragmentArgs {
        return args
    }
}