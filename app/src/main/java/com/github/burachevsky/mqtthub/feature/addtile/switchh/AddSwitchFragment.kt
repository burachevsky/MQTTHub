package com.github.burachevsky.mqtthub.feature.addtile.switchh

import androidx.fragment.app.viewModels
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.github.burachevsky.mqtthub.feature.addtile.AddTileFragment
import com.github.burachevsky.mqtthub.feature.addtile.AddTileModule
import javax.inject.Inject

class AddSwitchFragment : AddTileFragment<AddSwitchViewModel>() {

    @Inject
    override lateinit var viewModelFactory: ViewModelFactory<AddSwitchViewModel>

    override val viewModel: AddSwitchViewModel by viewModels { viewModelFactory }

    override fun inject() {
        appComponent.addTileComponent(AddTileModule(this))
            .inject(this)
    }
}