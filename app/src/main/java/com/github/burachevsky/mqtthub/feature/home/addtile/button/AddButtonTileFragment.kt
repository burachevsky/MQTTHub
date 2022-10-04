package com.github.burachevsky.mqtthub.feature.home.addtile.button

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.burachevsky.mqtthub.common.container.UIContainer
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.common.ext.collectOnStarted
import com.github.burachevsky.mqtthub.common.ext.get
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.common.recycler.CompositeAdapter
import com.github.burachevsky.mqtthub.common.widget.ButtonItemAdapter
import com.github.burachevsky.mqtthub.common.widget.InputFieldItemAdapter
import com.github.burachevsky.mqtthub.databinding.FragmentAddTileBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import javax.inject.Inject

class AddButtonTileFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<AddButtonTileViewModel>

    lateinit var viewModel: AddButtonTileViewModel

    private val container = UIContainer(this, ::Navigator)

    private var _binding: FragmentAddTileBinding? = null
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
        appComponent.addButtonTileComponent(AddButtonTileModule(this))
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
        _binding = FragmentAddTileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        container.onViewCreated(viewModel.container, this)

        binding.recyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            adapter = listAdapter
        }

        binding.toolbar.setTitle(viewModel.title)

        collectOnStarted(viewModel.items, listAdapter::submitList)

        collectOnStarted(viewModel.itemsChanged) {
            listAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}