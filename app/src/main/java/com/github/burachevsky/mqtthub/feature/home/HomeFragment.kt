package com.github.burachevsky.mqtthub.feature.home

import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.content.getSystemService
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.burachevsky.mqtthub.AppActivity
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.container.DependableOnStatusBarHeight
import com.github.burachevsky.mqtthub.common.container.ViewController
import com.github.burachevsky.mqtthub.common.container.viewContainer
import com.github.burachevsky.mqtthub.domain.eventbus.AppEventHandler
import com.github.burachevsky.mqtthub.domain.eventbus.AppEvent
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.common.ext.changeBackgroundColor
import com.github.burachevsky.mqtthub.common.ext.collectOnStarted
import com.github.burachevsky.mqtthub.common.ext.verticalLinearLayoutManager
import com.github.burachevsky.mqtthub.common.recycler.CompositeAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemMoveCallback
import com.github.burachevsky.mqtthub.databinding.FragmentHomeBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.github.burachevsky.mqtthub.feature.home.drawer.HomeDrawerManager
import com.github.burachevsky.mqtthub.feature.home.item.*
import com.github.burachevsky.mqtthub.feature.home.drawer.item.DrawerLabelItem
import com.github.burachevsky.mqtthub.feature.home.drawer.item.DrawerLabelItemAdapter
import com.github.burachevsky.mqtthub.feature.home.drawer.item.DrawerMenuItem
import com.github.burachevsky.mqtthub.feature.home.drawer.item.DrawerMenuItemAdapter
import com.github.burachevsky.mqtthub.feature.home.item.tile.ButtonTileItemAdapter
import com.github.burachevsky.mqtthub.feature.home.item.tile.ChartTileItemAdapter
import com.github.burachevsky.mqtthub.feature.home.item.tile.SliderTileItem
import com.github.burachevsky.mqtthub.feature.home.item.tile.SliderTileItemAdapter
import com.github.burachevsky.mqtthub.feature.home.item.tile.SwitchTileItemAdapter
import com.github.burachevsky.mqtthub.feature.home.item.tile.TextTileItemAdapter
import com.github.burachevsky.mqtthub.feature.home.item.tile.TextTileItemViewHolder
import com.github.burachevsky.mqtthub.feature.tiledetails.text.TextTileDetailsFragmentArgs
import com.google.android.material.elevation.SurfaceColors
import javax.inject.Inject

class HomeFragment : Fragment(R.layout.fragment_home),
    ViewController<HomeViewModel>, AppEventHandler, DependableOnStatusBarHeight {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<HomeViewModel>

    override val binding by viewBinding(FragmentHomeBinding::bind)
    override val viewModel: HomeViewModel by viewModels { viewModelFactory }
    override val container by viewContainer()

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
        ChartTileItemAdapter(tileItemListener),
        SliderTileItemAdapter(
            object : SliderTileItem.Listener, TileItem.Listener by tileItemListener {
                override fun sliderValueChanged(position: Int, value: Float) {
                    viewModel.sliderValueChanged(position, value)
                }
            }
        ),
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
            vibrateAsEditModeChanged()
            viewModel.navigateUp()
        }
    }

    private val colorSurface by lazy(LazyThreadSafetyMode.NONE) {
        SurfaceColors.SURFACE_0.getColor(requireContext())
    }

    private val colorSurface2 by lazy(LazyThreadSafetyMode.NONE) {
        SurfaceColors.SURFACE_2.getColor(requireContext())
    }

    private var wasEditing = false

    private var toolbarIsColored = false
    private var currentToolbarAnimation: ValueAnimator? = null

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (!recyclerView.canScrollVertically(-1)) {
                scrollChanged(recyclerViewIsAtStart = true)
            } else {
                scrollChanged(recyclerViewIsAtStart = false)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.homeComponent(HomeModule(this))
            .inject(this)
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.root.setOnApplyWindowInsetsListener { v, insets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
                fitStatusBarHeight(statusBarInsets.top)
            } else {
                fitStatusBarHeight(insets.systemWindowInsetTop)
            }

            insets
        }

        binding.toolbarLayout.setBackgroundColor(colorSurface)
        binding.bottomAppBar.setBackgroundColor(colorSurface2)

        drawerManager.fillDrawer()

        setupListeners()
        observeViewModel()
        setupRecyclerView()
        setupDrawerRecyclerView()

    }

    override fun fitStatusBarHeight(statusBarHeight: Int) {
        (requireActivity() as AppActivity).statusBarHeight = statusBarHeight
        binding.drawerRecyclerView.updatePadding(
            top = statusBarHeight
        )
        binding.toolbarLayout.updatePadding(
            top = statusBarHeight
        )
    }

    override fun onStart() {
        super.onStart()
        postponeEnterTransition()
    }

    override fun onResume() {
        super.onResume()
        backPressedCallback.isEnabled = true
        binding.bottomAppBar.isVisible = !viewModel.editMode.value.isEditMode
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

                findNavController().navigate(
                    R.id.navigateTextTileDetails,
                    TextTileDetailsFragmentArgs(effect.tileId).toBundle(),
                    null,
                    FragmentNavigatorExtras(
                        viewHolder.binding.tile to "detailsTile",
                        viewHolder.binding.tileName to "detailsTileName",
                        viewHolder.binding.tilePayload to "detailsPayloadName",
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
            itemAnimator = DefaultItemAnimator().apply {
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
        binding.recyclerView.apply {
            removeOnScrollListener(scrollListener)
            addOnScrollListener(scrollListener)
        }

        binding.connection.setOnClickListener {
            viewModel.connectionClicked()
        }

        binding.toolbar.setNavigationOnClickListener {
            if (viewModel.editMode.value.isEditMode) {
                vibrateAsEditModeChanged()
                viewModel.navigateUp()
            } else {
                binding.drawerLayout.open()
            }
        }

        binding.bottomAppBarToolbar.setNavigationOnClickListener {
            viewModel.showOptionsMenu()
        }

        binding.toolbar.setOnMenuItemClickListener {
            handleContextMenuAction(it.itemId)
        }

        binding.bottomAppBarToolbar.setOnMenuItemClickListener {
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

        collectOnStarted(viewModel.items) {
            listAdapter.submitList(it)

            binding.root.doOnPreDraw {
                startPostponedEnterTransition()
            }
        }

        collectOnStarted(drawerManager.items, drawerListAdapter::submitList)
        collectOnStarted(viewModel.editMode, ::bindEditMode)
        collectOnStarted(viewModel.title) {
            if (!viewModel.editMode.value.isEditMode) {
                binding.toolbar.title = it
            }
        }

        collectOnStarted(viewModel.noTilesYet) {
            binding.noTilesText.isVisible = it
            binding.recyclerView.isVisible = !it
        }

        collectOnStarted(viewModel.noBrokersYet) {
            binding.addFirstBrokerButton.isVisible = it
            if (it) {
                binding.noTilesText.isVisible = false
            }
            binding.bottomAppBar.isVisible = !it
            binding.recyclerView.isVisible = !it
        }
    }

    private fun makeItemMoveCallback(): ItemMoveCallback {
        return ItemMoveCallback(
            object  : ItemMoveCallback.ItemTouchHelperContract {
                override fun onItemMoved(fromPosition: Int, toPosition: Int) {
                    viewModel.moveItem(positionFrom = fromPosition, positionTo = toPosition)
                }

                override fun onItemSelected(position: Int) {
                    viewModel.tileLongClicked(position)
                }

                override fun onItemReleased(position: Int) {
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
            R.id.edit_mode -> {
                vibrateAsEditModeChanged()
                viewModel.editModeClicked()
            }
            R.id.delete -> viewModel.deleteTilesClicked()
            R.id.duplicate -> viewModel.duplicateTileClicked()
            R.id.newTile -> viewModel.addTileClicked()
            else -> return false
        }
        return true
    }

    private fun scrollChanged(recyclerViewIsAtStart: Boolean) {
        if (viewModel.editMode.value.isEditMode) {
            if (recyclerViewIsAtStart) {
                if (toolbarIsColored) {
                    toolbarIsColored = false
                    currentToolbarAnimation?.cancel()
                    currentToolbarAnimation = binding.toolbarLayout
                        .changeBackgroundColor(colorSurface)
                }

            } else {
                if (!toolbarIsColored) {
                    toolbarIsColored = true
                    currentToolbarAnimation?.cancel()
                    currentToolbarAnimation = binding.toolbarLayout
                        .changeBackgroundColor(colorSurface2, timeMillis = 50)
                }
            }
        }
    }

    private fun bindEditMode(editMode: EditModeState) {
        val showEditToolbar = editMode.isEditMode

        if (showEditToolbar) {
            if (!wasEditing) {
                if (binding.recyclerView.canScrollVertically(-1)) {
                    binding.toolbarLayout.setBackgroundColor(colorSurface2)

                }
            }

            binding.toolbar.setNavigationIcon(R.drawable.ic_close)

            binding.toolbar.title = requireContext()
                .getString(R.string.tiles_selected, editMode.selectedCount)

        } else {
            binding.toolbarLayout.setBackgroundColor(colorSurface)

            binding.toolbar.title = viewModel.title.value
            binding.toolbar.setNavigationIcon(R.drawable.ic_menu)
        }

        binding.bottomAppBar.isVisible = !showEditToolbar

        wasEditing = showEditToolbar

        binding.drawerLayout.setDrawerLockMode(
            if (showEditToolbar) LOCK_MODE_LOCKED_CLOSED else LOCK_MODE_UNLOCKED
        )

        binding.toolbar.menu.clear()

        if (editMode.isEditMode) {
            val menuRes = when (viewModel.editMode.value.selectedCount) {
                0 -> R.menu.home_edit_mode_0_selected_menu
                1 -> R.menu.home_edit_mode_1_selected_menu
                else -> R.menu.home_edit_mode_menu
            }

            binding.toolbar.inflateMenu(menuRes)
        }
    }

    private fun vibrateAsEditModeChanged() {
        requireContext().getSystemService<Vibrator>()
            ?.vibrate(VibrationEffect.createOneShot(10, 255))
    }
}