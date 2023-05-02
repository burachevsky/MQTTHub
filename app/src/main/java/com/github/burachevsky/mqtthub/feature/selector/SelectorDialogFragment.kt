package com.github.burachevsky.mqtthub.feature.selector

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.container.ViewController
import com.github.burachevsky.mqtthub.common.container.viewContainer
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.common.ext.verticalLinearLayoutManager
import com.github.burachevsky.mqtthub.common.recycler.CompositeAdapter
import com.github.burachevsky.mqtthub.databinding.FragmentSelectorBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.elevation.SurfaceColors
import javax.inject.Inject

class SelectorDialogFragment : BottomSheetDialogFragment(R.layout.fragment_selector),
    ViewController<SelectorViewModel> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<SelectorViewModel>

    override val binding  by viewBinding(FragmentSelectorBinding::bind)
    override val viewModel: SelectorViewModel by viewModels { viewModelFactory }
    override val container by viewContainer()

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.root.setBackgroundColor(SurfaceColors.SURFACE_2.getColor(requireContext()))

        viewModel.title?.get(requireContext()).let { title ->
            binding.title.isVisible = !title.isNullOrEmpty()
            binding.title.text = title
        }

        binding.recyclerView.apply {
            layoutManager = verticalLinearLayoutManager()
            adapter = listAdapter.also {
                it.submitList(viewModel.items)
            }
        }
    }
}