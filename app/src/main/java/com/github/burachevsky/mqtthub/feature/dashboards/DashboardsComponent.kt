package com.github.burachevsky.mqtthub.feature.dashboards

import com.github.burachevsky.mqtthub.di.FragmentScope
import dagger.Subcomponent

@FragmentScope
@Subcomponent(modules = [DashboardsModule::class])
interface DashboardsComponent {

    fun inject(fragment: DashboardsFragment)
}