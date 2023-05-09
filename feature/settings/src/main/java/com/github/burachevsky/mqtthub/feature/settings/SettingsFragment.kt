package com.github.burachevsky.mqtthub.feature.settings

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.burachevsky.mqtthub.core.ui.container.ViewController
import com.github.burachevsky.mqtthub.core.ui.container.viewContainer
import com.github.burachevsky.mqtthub.core.ui.di.ViewModelFactory
import com.github.burachevsky.mqtthub.core.ui.ext.applicationAs
import com.github.burachevsky.mqtthub.core.ui.ext.collectOnStarted
import com.github.burachevsky.mqtthub.core.ui.ext.verticalLinearLayoutManager
import com.github.burachevsky.mqtthub.core.ui.recycler.CompositeAdapter
import com.github.burachevsky.mqtthub.core.ui.widget.SubtitleItemAdapter
import com.github.burachevsky.mqtthub.core.ui.widget.SwitchItemAdapter
import com.github.burachevsky.mqtthub.core.ui.widget.ToggleGroupItem
import com.github.burachevsky.mqtthub.core.ui.widget.ToggleGroupItemItemAdapter
import com.github.burachevsky.mqtthub.feature.settings.databinding.FragmentSettingsBinding
import javax.inject.Inject

class SettingsFragment : Fragment(R.layout.fragment_settings),
    ViewController<SettingsViewModel> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<SettingsViewModel>

    override val binding by viewBinding(FragmentSettingsBinding::bind)
    override val viewModel: SettingsViewModel by viewModels { viewModelFactory }
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
        applicationAs<SettingsComponent.Provider>().settingsComponent()
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.apply {
            layoutManager = verticalLinearLayoutManager()
            adapter = listAdapter
            setHasFixedSize(true)
        }

        collectOnStarted(viewModel.items, listAdapter::submitList)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.recreate()
    }
}