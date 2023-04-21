package com.github.burachevsky.mqtthub.feature.brokers

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.burachevsky.mqtthub.common.container.ViewContainer
import com.github.burachevsky.mqtthub.common.container.ViewController
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.common.ext.collectOnStarted
import com.github.burachevsky.mqtthub.common.ext.verticalLinearLayoutManager
import com.github.burachevsky.mqtthub.common.recycler.CompositeAdapter
import com.github.burachevsky.mqtthub.databinding.FragmentBrokersBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.github.burachevsky.mqtthub.feature.brokers.item.BrokerItem
import com.github.burachevsky.mqtthub.feature.brokers.item.BrokerItemAdapter
import javax.inject.Inject

class BrokersFragment : Fragment(), ViewController<BrokersViewModel> {

    private var _binding: FragmentBrokersBinding? = null
    private val binding get() = _binding!!

    override val container = ViewContainer(this, ::BrokersNavigator)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<BrokersViewModel>

    override val viewModel: BrokersViewModel by viewModels { viewModelFactory }

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
        appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        container.onCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBrokersBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.apply {
            layoutManager = verticalLinearLayoutManager()
            adapter = listAdapter
        }

        collectOnStarted(viewModel.items, listAdapter::submitList)

        binding.addBrokersButton.setOnClickListener {
            viewModel.addBrokerClicked()
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        collectOnStarted(viewModel.noBrokersYet) {
            binding.noBrokersText.isVisible = it
            binding.recyclerView.isVisible = !it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
