package com.github.burachevsky.mqtthub.feature.home.typeselector

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.burachevsky.mqtthub.common.container.UIContainer
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.common.recycler.CompositeAdapter
import com.github.burachevsky.mqtthub.databinding.FragmentSelectTileTypeBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject

class SelectTileTypeDialogFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<SelectTileTypeViewModel>

    lateinit var viewModel: SelectTileTypeViewModel

    private val container = UIContainer(this, ::Navigator)

    private var _binding: FragmentSelectTileTypeBinding? = null
    private val binding get() = _binding!!

    private val listAdapter = CompositeAdapter(
        TileTypeItemAdapter(
            object : TileTypeItem.Listener {
                override fun onClick(position: Int) {
                    viewModel.tileTypeClicked(position)
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
        viewModel = ViewModelProvider(this, viewModelFactory).get()
        container.onCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectTileTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        container.onViewCreated(viewModel.container, this)

        binding.recyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            adapter = listAdapter.also {
                it.submitList(viewModel.items)
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}