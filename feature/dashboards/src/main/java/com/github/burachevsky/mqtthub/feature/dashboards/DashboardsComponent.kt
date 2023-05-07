package com.github.burachevsky.mqtthub.feature.dashboards

import com.github.burachevsky.mqtthub.core.ui.di.FragmentScope
import dagger.Subcomponent

@FragmentScope
@Subcomponent(modules = [DashboardsModule::class])
interface DashboardsComponent {

    fun inject(fragment: DashboardsFragment)

    interface Provider {
        fun dashboardsComponent(module: DashboardsModule): DashboardsComponent
    }
}