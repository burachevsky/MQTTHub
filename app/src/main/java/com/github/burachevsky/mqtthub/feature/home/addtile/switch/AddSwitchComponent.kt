package com.github.burachevsky.mqtthub.feature.home.addtile.switch

import dagger.Subcomponent

@Subcomponent(modules = [AddSwitchModule::class])
interface AddSwitchComponent {
    fun inject(fragment: AddSwitchFragment)
}