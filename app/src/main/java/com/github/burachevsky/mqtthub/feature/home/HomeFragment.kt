package com.github.burachevsky.mqtthub.feature.home

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.container.ViewContainer
import com.github.burachevsky.mqtthub.common.container.ViewController
import com.github.burachevsky.mqtthub.domain.eventbus.AppEventHandler
import com.github.burachevsky.mqtthub.domain.eventbus.AppEvent
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.common.ext.collectOnStarted
import com.github.burachevsky.mqtthub.common.ext.verticalLinearLayoutManager
import com.github.burachevsky.mqtthub.common.recycler.CompositeAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemMoveCallback
import com.github.burachevsky.mqtthub.databinding.FragmentHomeBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.github.burachevsky.mqtthub.feature.home.item.*
import com.github.burachevsky.mqtthub.feature.tiledetails.text.TextTileDetailsFragmentArgs
import timber.log.Timber
import javax.inject.Inject

class HomeFragment : Fragment(), ViewController<HomeViewModel>, AppEventHandler {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override val container = ViewContainer(this, ::HomeNavigator)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<HomeViewModel>

    override val viewModel: HomeViewModel by viewModels { viewModelFactory }

    @Inject
    lateinit var drawerManager: HomeDrawerManager

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

    private val drawerListAdapter = CompositeAdapter(
        DrawerLabelItemAdapter(
            object : DrawerLabelItem.Listener {
                override fun onClick(position: Int) {
                    drawerManager.onLabelButtonClick(position)
                }
            }
        ),
        DrawerMenuItemAdapter(
            object : DrawerMenuItem.Listener {
                override fun onClick(position: Int) {
                    drawerManager.onMenuItemClick(position)
                }
            }
        ),
    )

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewModel.navigateUp()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.homeComponent(HomeModule(this))
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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        drawerManager.fillDrawer()
        setupListeners()
        observeViewModel()
        setupRecyclerView()
        setupDrawerRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        backPressedCallback.isEnabled = true
    }

    override fun onPause() {
        super.onPause()
        backPressedCallback.isEnabled = false
    }

    override fun handleEffect(effect: AppEvent): Boolean {
        when (effect) {
            is CloseHomeDrawer -> {
                binding.drawerLayout.close()
                return true
            }

            is CloseHomeDrawerOrNavigateUp -> {
                if (binding.drawerLayout.isOpen) {
                    binding.drawerLayout.close()
                } else {
                    backPressedCallback.isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                return true
            }

            is OpenTextTileDetails -> {
                 val viewHolder = binding.recyclerView
                     .findViewHolderForAdapterPosition(effect.position)
                    as TextTileItemViewHolder

                viewHolder.binding

                findNavController().navigate(
                    R.id.navigateTextTileDetails,
                    TextTileDetailsFragmentArgs(effect.tileId).toBundle(),
                    null,
                    FragmentNavigatorExtras(
                        viewHolder.binding.tile to "detailsTile",
                        //viewHolder.binding.tileName to "detailsTileName",
                    )
                )
            }
        }

        return false
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            addItemDecoration(TileLayoutDecoration(requireContext()))
            layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )

            setItemViewCacheSize(10)
            itemAnimator =
                DefaultItemAnimator().apply {
                supportsChangeAnimations = false
                addDuration = 0
            }
            setHasFixedSize(true)
            adapter = listAdapter
        }

        val moveCallback = makeItemMoveCallback()
        val touchHelper = ItemTouchHelper(moveCallback)
        touchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun setupDrawerRecyclerView() {
        binding.drawerRecyclerView.apply {
            layoutManager = verticalLinearLayoutManager()
            setHasFixedSize(true)
            adapter = drawerListAdapter
        }
    }

    private fun setupListeners() {
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
            binding.drawerLayout.open()
        }

        binding.editModeToolbar.setOnMenuItemClickListener {
            handleContextMenuAction(it.itemId)
        }

        binding.addFirstBrokerButton.setOnClickListener {
            viewModel.addFirstBroker()
        }

        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, backPressedCallback)
    }

    private fun observeViewModel() {
        collectOnStarted(viewModel.connectionState, binding.connection::applyState)
        collectOnStarted(viewModel.items, listAdapter::submitList)
        collectOnStarted(drawerManager.items, drawerListAdapter::submitList)
        collectOnStarted(viewModel.editMode, ::bindEditMode)
        collectOnStarted(viewModel.title, binding.toolbar::setTitle)

        collectOnStarted(viewModel.noTilesYet) {
            binding.noTilesText.isVisible = it
            binding.recyclerView.isVisible = !it
        }

        collectOnStarted(viewModel.noBrokersYet) {
            binding.addFirstBrokerButton.isVisible = it
            if (it) {
                binding.noTilesText.isVisible = false
            }
            binding.addTileButton.apply {
                if (it) hide() else show()
            }
            binding.recyclerView.isVisible = !it
        }
    }

    private fun makeItemMoveCallback(): ItemMoveCallback {
        return ItemMoveCallback(
            object  : ItemMoveCallback.ItemTouchHelperContract {
                override fun onItemMoved(fromPosition: Int, toPosition: Int) {
                    Timber.d(
                        "onItemMoved(fromPosition = $fromPosition, toPosition = $toPosition)"
                    )
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
    }

    private fun handleContextMenuAction(id: Int?): Boolean {
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

        binding.editModeToolbar.isVisible = showEditToolbar
        binding.toolbarLayout.isVisible = !showEditToolbar

        binding.editModeToolbar.title = requireContext()
            .getString(R.string.tiles_selected, editMode.selectedCount)

        binding.addTileButton.run {
            if (showEditToolbar) hide() else show()
        }

        binding.drawerLayout.setDrawerLockMode(
            if (showEditToolbar) LOCK_MODE_LOCKED_CLOSED else LOCK_MODE_UNLOCKED
        )

        if (editMode.isEditMode) {
            val menuRes = if (viewModel.editMode.value.selectedCount == 1) {
                R.menu.tile_item_menu
            } else {
                R.menu.home_edit_mode_menu
            }

            binding.editModeToolbar.run {
                menu.clear()
                inflateMenu(menuRes)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}