package com.github.burachevsky.mqtthub.feature.home.addtile.switchh

import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.github.burachevsky.mqtthub.feature.home.addtile.AddTileFragment
import com.github.burachevsky.mqtthub.feature.home.addtile.AddTileModule
import javax.inject.Inject

class AddSwitchFragment : AddTileFragment<AddSwitchViewModel>(AddSwitchViewModel::class) {

    @Inject
    override lateinit var viewModelFactory: ViewModelFactory<AddSwitchViewModel>

    override fun inject() {
        appComponent.addTileComponent(AddTileModule(this))
            .inject(this)
    }
}