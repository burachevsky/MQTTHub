package com.github.burachevsky.mqtthub.feature.home.addtile.text

import androidx.fragment.app.viewModels
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.github.burachevsky.mqtthub.feature.home.addtile.AddTileFragment
import com.github.burachevsky.mqtthub.feature.home.addtile.AddTileModule
import javax.inject.Inject

class AddTextTileFragment : AddTileFragment<AddTextTileViewModel>(AddTextTileViewModel::class) {

    @Inject
    override lateinit var viewModelFactory: ViewModelFactory<AddTextTileViewModel>

    override val viewModel: AddTextTileViewModel by viewModels { viewModelFactory }

    override fun inject() {
        appComponent.addTileComponent(AddTileModule(this))
            .inject(this)
    }
}