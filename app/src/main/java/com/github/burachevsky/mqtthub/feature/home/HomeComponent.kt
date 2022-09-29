package com.github.burachevsky.mqtthub.feature.home

import com.github.burachevsky.mqtthub.di.AppComponent
import com.github.burachevsky.mqtthub.di.FragmentScope
import dagger.BindsInstance
import dagger.Component

@FragmentScope
@Component(dependencies = [AppComponent::class])
interface HomeComponent {

    @Component.Factory
    interface Factory {
        fun create(
            appComponent: AppComponent,
            @BindsInstance args: HomeFragmentArgs,
        ): HomeComponent
    }

    fun inject(fragment: HomeFragment)
}