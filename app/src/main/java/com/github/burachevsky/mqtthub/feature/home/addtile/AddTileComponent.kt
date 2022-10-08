package com.github.burachevsky.mqtthub.feature.home.addtile

import com.github.burachevsky.mqtthub.feature.home.addtile.button.AddButtonTileFragment
import com.github.burachevsky.mqtthub.feature.home.addtile.switchh.AddSwitchFragment
import com.github.burachevsky.mqtthub.feature.home.addtile.text.AddTextTileFragment
import dagger.Subcomponent

@Subcomponent(modules = [AddTileModule::class])
interface AddTileComponent {
    fun inject(fragment: AddButtonTileFragment)
    fun inject(fragment: AddSwitchFragment)
    fun inject(fragment: AddTextTileFragment)
}