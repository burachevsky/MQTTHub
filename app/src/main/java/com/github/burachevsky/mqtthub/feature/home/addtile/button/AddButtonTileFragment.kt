package com.github.burachevsky.mqtthub.feature.home.addtile.button

import androidx.fragment.app.viewModels
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.github.burachevsky.mqtthub.feature.home.addtile.AddTileFragment
import com.github.burachevsky.mqtthub.feature.home.addtile.AddTileModule
import javax.inject.Inject

class AddButtonTileFragment :
    AddTileFragment<AddButtonTileViewModel>(AddButtonTileViewModel::class) {

    @Inject
    override lateinit var viewModelFactory: ViewModelFactory<AddButtonTileViewModel>

    override val viewModel: AddButtonTileViewModel by viewModels { viewModelFactory }

    override fun inject() {
        appComponent.addTileComponent(AddTileModule(this))
            .inject(this)
    }
}