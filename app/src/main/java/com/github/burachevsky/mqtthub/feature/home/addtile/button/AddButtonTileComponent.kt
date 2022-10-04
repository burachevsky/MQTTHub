package com.github.burachevsky.mqtthub.feature.home.addtile.button

import dagger.Subcomponent

@Subcomponent(modules = [AddButtonTileModule::class])
interface AddButtonTileComponent {
    fun inject(fragment: AddButtonTileFragment)
}