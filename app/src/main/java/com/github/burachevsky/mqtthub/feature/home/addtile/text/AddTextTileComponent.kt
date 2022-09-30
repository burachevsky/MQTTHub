package com.github.burachevsky.mqtthub.feature.home.addtile.text

import dagger.Subcomponent

@Subcomponent(modules = [AddTextTileModule::class])
interface AddTextTileComponent {
    fun inject(fragment: AddTextTileFragment)
}