package com.github.burachevsky.mqtthub.feature.addtile.chart

import androidx.fragment.app.viewModels
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.github.burachevsky.mqtthub.feature.addtile.AddTileFragment
import com.github.burachevsky.mqtthub.feature.addtile.AddTileModule
import javax.inject.Inject

class AddChartTileFragment : AddTileFragment<AddChartTileViewModel>() {

    @Inject
    override lateinit var viewModelFactory: ViewModelFactory<AddChartTileViewModel>

    override val viewModel: AddChartTileViewModel by viewModels { viewModelFactory }

    override fun inject() {
        appComponent.addTileComponent(AddTileModule(this))
            .inject(this)
    }
}