package com.github.burachevsky.mqtthub.feature.brokers

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
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
import com.github.burachevsky.mqtthub.feature.brokers.databinding.FragmentBrokersBinding
import com.github.burachevsky.mqtthub.feature.brokers.item.BrokerItem
import com.github.burachevsky.mqtthub.feature.brokers.item.BrokerItemAdapter
import javax.inject.Inject

class BrokersFragment : Fragment(R.layout.fragment_brokers),
    ViewController<BrokersViewModel> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<BrokersViewModel>

    override val binding by viewBinding(FragmentBrokersBinding::bind)
    override val viewModel: BrokersViewModel by viewModels { viewModelFactory }
    override val container by viewContainer()

    private val listAdapter = CompositeAdapter(
        BrokerItemAdapter(
            object : BrokerItem.Listener {
                override fun onClick(position: Int) {
                    viewModel.brokerClicked(position)
                }

                override fun onEditClick(position: Int) {
                    viewModel.editBrokerClicked(position)
                }

                override fun onDeleteClick(position: Int) {
                    viewModel.deleteBrokerClicked(position)
                }
            }
        )
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        applicationAs<BrokersComponent.Provider>().brokersComponent()
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.apply {
            layoutManager = verticalLinearLayoutManager()
            adapter = listAdapter
        }

        binding.addBrokersButton.setOnClickListener {
            viewModel.addBrokerClicked()
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        collectOnStarted(viewModel.items, listAdapter::submitList)

        collectOnStarted(viewModel.noBrokersYet) {
            binding.noBrokersText.isVisible = it
            binding.recyclerView.isVisible = !it
        }
    }
}
