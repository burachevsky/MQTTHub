package com.github.burachevsky.mqtthub.feature.home.addtile.switchh

import androidx.fragment.app.viewModels
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.github.burachevsky.mqtthub.feature.home.addtile.AddTileFragment
import com.github.burachevsky.mqtthub.feature.home.addtile.AddTileModule
import javax.inject.Inject

class AddSwitchFragment : AddTileFragment<AddSwitchViewModel>(AddSwitchViewModel::class) {

    @Inject
    override lateinit var viewModelFactory: ViewModelFactory<AddSwitchViewModel>

    override val viewModel: AddSwitchViewModel by viewModels { viewModelFactory }

    override fun inject() {
        appComponent.addTileComponent(AddTileModule(this))
            .inject(this)
    }
}