package com.github.burachevsky.mqtthub.feature.addtile

import com.github.burachevsky.mqtthub.feature.addtile.button.AddButtonTileFragment
import com.github.burachevsky.mqtthub.feature.addtile.chart.AddChartTileFragment
import com.github.burachevsky.mqtthub.feature.addtile.slider.AddSliderTileFragment
import com.github.burachevsky.mqtthub.feature.addtile.switchh.AddSwitchFragment
import com.github.burachevsky.mqtthub.feature.addtile.text.AddTextTileFragment
import dagger.Subcomponent

@Subcomponent(modules = [AddTileModule::class])
interface AddTileComponent {
    fun inject(fragment: AddButtonTileFragment)
    fun inject(fragment: AddSwitchFragment)
    fun inject(fragment: AddTextTileFragment)
    fun inject(fragment: AddChartTileFragment)
    fun inject(fragment: AddSliderTileFragment)

    interface Provider {
        fun addTileComponent(module: AddTileModule): AddTileComponent
    }
}