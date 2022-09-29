package com.github.burachevsky.mqtthub.feature.brokers

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.burachevsky.mqtthub.common.container.UIContainer
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.common.ext.collectOnStarted
import com.github.burachevsky.mqtthub.common.recycler.ListAdapter
import com.github.burachevsky.mqtthub.databinding.FragmentBrokersBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.github.burachevsky.mqtthub.feature.brokers.item.BrokerItem
import com.github.burachevsky.mqtthub.feature.brokers.item.BrokerItemAdapter
import javax.inject.Inject

class BrokersFragment : Fragment() {

    private var _binding: FragmentBrokersBinding? = null
    private val binding get() = _binding!!

    private val container = UIContainer(this, ::BrokersNavigator)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<BrokersViewModel>

    lateinit var viewModel: BrokersViewModel

    private val listAdapter = ListAdapter(
        BrokerItemAdapter(
            object : BrokerItem.Listener {
                override fun onClick(position: Int) {
                    viewModel.brokerClicked(position)
                }

                override fun onEditClick(position: Int) {
                    viewModel.editBrokerClicked(position)
                }

                override fun onLongClick(position: Int) {
                    viewModel.showBrokerRemoveDialog(position)
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
        viewModel = ViewModelProvider(this, viewModelFactory)[BrokersViewModel::class.java]
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
        container.onViewCreated(viewModel.container, this)

        binding.recyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            adapter = listAdapter
        }

        collectOnStarted(viewModel.items, listAdapter::submitList)

        binding.addBrokersButton.setOnClickListener {
            viewModel.addBrokerClicked()
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
