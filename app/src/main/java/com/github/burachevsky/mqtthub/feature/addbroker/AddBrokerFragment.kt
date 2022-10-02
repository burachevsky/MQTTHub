package com.github.burachevsky.mqtthub.feature.addbroker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.burachevsky.mqtthub.common.container.UIContainer
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.common.ext.collectOnStarted
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.common.recycler.CompositeAdapter
import com.github.burachevsky.mqtthub.common.widget.ButtonItemAdapter
import com.github.burachevsky.mqtthub.common.widget.InputFieldItemAdapter
import com.github.burachevsky.mqtthub.databinding.FragmentAddBrokerBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import javax.inject.Inject

class AddBrokerFragment : Fragment() {

    private val container = UIContainer(this, ::Navigator)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<AddBrokerViewModel>

    lateinit var viewModel: AddBrokerViewModel

    private var _binding: FragmentAddBrokerBinding? = null
    private val binding get() = _binding!!

    private val listAdapter = CompositeAdapter(
        InputFieldItemAdapter(),
        ButtonItemAdapter(
            listener = {
                viewModel.saveResult()
            }
        )
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.addBrokerComponent(AddBrokerModule(this))
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get()
        container.onCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBrokerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        container.onViewCreated(viewModel.container, this)

        binding.toolbar.setTitle(viewModel.title)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = listAdapter
        }

        collectOnStarted(viewModel.items, listAdapter::submitList)

        collectOnStarted(viewModel.itemsChanged) {
            listAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
