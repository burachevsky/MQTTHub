package com.github.burachevsky.mqtthub.feature.settings

import com.github.burachevsky.mqtthub.core.ui.di.FragmentScope
import dagger.Subcomponent

@FragmentScope
@Subcomponent
interface SettingsComponent {

    fun inject(fragment: SettingsFragment)

    interface Provider {
        fun settingsComponent(): SettingsComponent
    }
}