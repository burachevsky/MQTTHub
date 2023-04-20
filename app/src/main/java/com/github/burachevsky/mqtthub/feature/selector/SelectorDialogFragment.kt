package com.github.burachevsky.mqtthub.feature.selector

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.github.burachevsky.mqtthub.common.container.ViewContainer
import com.github.burachevsky.mqtthub.common.container.ViewController
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.common.ext.verticalLinearLayoutManager
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.common.recycler.CompositeAdapter
import com.github.burachevsky.mqtthub.databinding.FragmentSelectorBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject

class SelectorDialogFragment : BottomSheetDialogFragment(),
    ViewController<SelectorViewModel> {

    private var _binding: FragmentSelectorBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<SelectorViewModel>

    override val viewModel: SelectorViewModel by viewModels { viewModelFactory }

    override val container = ViewContainer(this, ::Navigator)

    private val listAdapter = CompositeAdapter(
        TileTypeItemAdapter(
            object : SelectorItem.Listener {
                override fun onClick(position: Int) {
                    viewModel.tileTypeClicked(position)
                }
            }
        )
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.selectorComponent(SelectorModule(this))
            .inject(this)
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
        _binding = FragmentSelectorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.title.text = viewModel.title.get(requireContext())

        binding.recyclerView.apply {
            layoutManager = verticalLinearLayoutManager()
            adapter = listAdapter.also {
                it.submitList(viewModel.items)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}