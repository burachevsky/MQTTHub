package com.github.burachevsky.mqtthub.feature.dashboards

import com.github.burachevsky.mqtthub.core.ui.constant.NavArg
import dagger.Module
import dagger.Provides

@Module
class DashboardsModule(fragment: DashboardsFragment) {
    private val addNew: Boolean = fragment.requireArguments().getBoolean(NavArg.ADD_NEW)

    @Provides
    fun provideArgs(): Boolean {
        return addNew
    }
}