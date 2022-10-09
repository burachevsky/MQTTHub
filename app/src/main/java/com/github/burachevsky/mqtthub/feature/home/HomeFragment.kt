package com.github.burachevsky.mqtthub.feature.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.container.UIContainer
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.common.ext.collectOnStarted
import com.github.burachevsky.mqtthub.common.ext.showPopupMenu
import com.github.burachevsky.mqtthub.common.recycler.CompositeAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemMoveCallback
import com.github.burachevsky.mqtthub.databinding.FragmentHomeBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.github.burachevsky.mqtthub.feature.home.item.*
import timber.log.Timber
import javax.inject.Inject

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val container = UIContainer(this, ::HomeNavigator)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<HomeViewModel>

    private lateinit var viewModel: HomeViewModel

    private val tileItemListener = object : TileItem.Listener {

        override fun onLongClick(position: Int): Boolean {
            return viewModel.tileLongClicked(position)
        }

        override fun onClick(position: Int) {
            viewModel.tileClicked(position)
        }
    }

    private val listAdapter = CompositeAdapter(
        TextTileItemAdapter(tileItemListener),
        ButtonTileItemAdapter(tileItemListener),
        SwitchTileItemAdapter(tileItemListener),
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.homeComponent(HomeModule(this))
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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        container.onViewCreated(viewModel.container, this)

        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        viewModel.navigateUp()
                    }
                }
            )

        bind()
        observeViewModel()
    }

    private fun bind() {
        binding.recyclerView.apply {
            addItemDecoration(TileLayoutDecoration(requireContext()))
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            adapter = listAdapter
        }

        binding.addTileButton.setOnClickListener {
            viewModel.addTileClicked()
        }

        binding.connection.setOnClickListener {
            viewModel.connectionClicked()
        }

        binding.editModeToolbar.setNavigationOnClickListener {
            viewModel.navigateUp()
        }

        binding.toolbar.setNavigationOnClickListener {
            viewModel.navigateUp()
        }

        val moveCallback = ItemMoveCallback(
            object  : ItemMoveCallback.ItemTouchHelperContract {
                override fun onItemMoved(fromPosition: Int, toPosition: Int) {
                    Timber.d("onItemMoved(fromPosition = $fromPosition, toPosition = $toPosition)")
                    viewModel.moveItem(positionFrom = fromPosition, positionTo = toPosition)
                }

                override fun onItemSelected(position: Int) {
                    Timber.d("onItemSelected(myViewHolder = $position)")
                    viewModel.tileLongClicked(position)
                }

                override fun onItemReleased(position: Int) {
                    Timber.d("onItemReleased(myViewHolder = $position)")
                    viewModel.commitReorder(position)
                }

                override fun canMove(): Boolean {
                    return viewModel.canMoveItem()
                }
            }
        )

        val touchHelper = ItemTouchHelper(moveCallback)
        touchHelper.attachToRecyclerView(binding.recyclerView)

        binding.editModeMore.setOnClickListener {
            it.showPopupMenu(
                if (viewModel.editMode.value.selectedCount == 1) {
                    R.menu.tile_item_menu
                } else {
                    R.menu.home_edit_mode_menu
                },
                onItemSelected = ::handleContextMenuAction
            )
        }
    }

    private fun observeViewModel() {
        collectOnStarted(viewModel.connectionState, binding.connection::applyState)

        collectOnStarted(viewModel.title) {
            binding.toolbar.title = it
        }

        collectOnStarted(viewModel.noTilesYet) {
            binding.noTilesText.isVisible = it
            binding.recyclerView.isVisible = !it
        }

        collectOnStarted(viewModel.items, listAdapter::submitList)

        collectOnStarted(viewModel.editMode, ::bindEditMode)
    }

    private fun handleContextMenuAction(id: Int): Boolean {
        when (id) {
            R.id.edit -> viewModel.editTileClicked()
            R.id.delete -> viewModel.deleteTilesClicked()
            R.id.duplicate -> viewModel.duplicateTileClicked()
            else -> return false
        }
        return true
    }

    private fun bindEditMode(editMode: EditModeState) {
        val showEditToolbar = editMode.isEditMode && !editMode.isMovingMode
        binding.editModeToolbarLayout.isVisible = showEditToolbar
        binding.addTileButton.run {
            if (showEditToolbar) hide() else show()
        }
        binding.toolbarLayout.isVisible = !showEditToolbar
        binding.editModeToolbar.title = "${editMode.selectedCount}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}