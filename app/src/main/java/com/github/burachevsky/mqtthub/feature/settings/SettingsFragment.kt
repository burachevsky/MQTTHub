package com.github.burachevsky.mqtthub.feature.settings

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.container.ViewController
import com.github.burachevsky.mqtthub.common.container.viewContainer
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.common.ext.collectOnStarted
import com.github.burachevsky.mqtthub.common.ext.verticalLinearLayoutManager
import com.github.burachevsky.mqtthub.common.recycler.CompositeAdapter
import com.github.burachevsky.mqtthub.common.widget.SubtitleItemAdapter
import com.github.burachevsky.mqtthub.common.widget.SwitchItemAdapter
import com.github.burachevsky.mqtthub.common.widget.ToggleGroupItem
import com.github.burachevsky.mqtthub.common.widget.ToggleGroupItemItemAdapter
import com.github.burachevsky.mqtthub.databinding.FragmentSettingsBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import javax.inject.Inject

class SettingsFragment : Fragment(R.layout.fragment_settings),
    ViewController<SettingsViewModel> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<SettingsViewModel>

    override val binding by viewBinding(FragmentSettingsBinding::bind)
    override val viewModel by viewModels<SettingsViewModel> { viewModelFactory }
    override val container by viewContainer()

    private val listAdapter = CompositeAdapter(
        SwitchItemAdapter(),
        SubtitleItemAdapter(),
        ToggleGroupItemItemAdapter(
            object : ToggleGroupItem.Listener {
                override fun onSelectionChanged(position: Int) {
                    viewModel.toggleGroupSelectionChanged(position)
                }
            }
        )
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.apply {
            layoutManager = verticalLinearLayoutManager()
            adapter = listAdapter
        }

        collectOnStarted(viewModel.items, listAdapter::submitList)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.recreate()
    }
}