package com.github.burachevsky.mqtthub.feature.addtile

import com.github.burachevsky.mqtthub.di.FragmentScope
import com.github.burachevsky.mqtthub.feature.dashboards.DashboardsFragment
import com.github.burachevsky.mqtthub.feature.dashboards.DashboardsModule
import dagger.Subcomponent

@FragmentScope
@Subcomponent(modules = [DashboardsModule::class])
interface DashboardsComponent {

    fun inject(fragment: DashboardsFragment)
}