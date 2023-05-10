package com.github.burachevsky.mqtthub.feature.home

import android.animation.ValueAnimator
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.burachevsky.mqtthub.core.eventbus.AppEvent
import com.github.burachevsky.mqtthub.core.eventbus.AppEventHandler
import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.constant.ContentType
import com.github.burachevsky.mqtthub.core.ui.constant.NavArg
import com.github.burachevsky.mqtthub.core.ui.constant.NavDestination
import com.github.burachevsky.mqtthub.core.ui.container.DependentOnSystemBarsSize
import com.github.burachevsky.mqtthub.core.ui.container.ViewController
import com.github.burachevsky.mqtthub.core.ui.container.viewContainer
import com.github.burachevsky.mqtthub.core.ui.di.ViewModelFactory
import com.github.burachevsky.mqtthub.core.ui.ext.applicationAs
import com.github.burachevsky.mqtthub.core.ui.ext.changeBackgroundColor
import com.github.burachevsky.mqtthub.core.ui.ext.collectOnStarted
import com.github.burachevsky.mqtthub.core.ui.ext.verticalLinearLayoutManager
import com.github.burachevsky.mqtthub.core.ui.recycler.CompositeAdapter
import com.github.burachevsky.mqtthub.core.ui.recycler.ItemMoveCallback
import com.github.burachevsky.mqtthub.feature.home.databinding.FragmentHomeBinding
import com.github.burachevsky.mqtthub.feature.home.item.TileItem
import com.github.burachevsky.mqtthub.feature.home.item.drawer.DrawerLabelItem
import com.github.burachevsky.mqtthub.feature.home.item.drawer.DrawerLabelItemAdapter
import com.github.burachevsky.mqtthub.feature.home.item.drawer.DrawerMenuItem
import com.github.burachevsky.mqtthub.feature.home.item.drawer.DrawerMenuItemAdapter
import com.github.burachevsky.mqtthub.feature.home.item.tile.ButtonTileItemAdapter
import com.github.burachevsky.mqtthub.feature.home.item.tile.ChartTileItemAdapter
import com.github.burachevsky.mqtthub.feature.home.item.tile.SliderTileItem
import com.github.burachevsky.mqtthub.feature.home.item.tile.SliderTileItemAdapter
import com.github.burachevsky.mqtthub.feature.home.item.tile.SwitchTileItemAdapter
import com.github.burachevsky.mqtthub.feature.home.item.tile.TextTileItemAdapter
import com.github.burachevsky.mqtthub.feature.home.item.tile.TextTileItemViewHolder
import com.google.android.material.elevation.SurfaceColors
import javax.inject.Inject
import com.github.burachevsky.mqtthub.feature.home.R as featureR

class HomeFragment : Fragment(featureR.layout.fragment_home),
    ViewController<HomeViewModel>, AppEventHandler, DependentOnSystemBarsSize {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<HomeViewModel>

    override val binding by viewBinding(FragmentHomeBinding::bind)
    override val viewModel: HomeViewModel by viewModels { viewModelFactory }
    override val container by viewContainer()

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
                    viewModel.onDrawerLabelButtonClick(position)
                }
            }
        ),
        DrawerMenuItemAdapter(
            object : DrawerMenuItem.Listener {
                override fun onClick(position: Int) {
                    viewModel.onDrawerMenuItemClick(position)
                }
            }
        ),
    )

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            vibrate()
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

    private val fileExporter = registerForActivityResult(
        ActivityResultContracts.CreateDocument(ContentType.JSON),
        ::fileForExportSelected
    )

    private val fileImporter = registerForActivityResult(
        ActivityResultContracts.GetContent(),
        ::fileForImportSelected
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        applicationAs<HomeComponent.Provider>().homeComponent(HomeModule(this))
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbarLayout.setBackgroundColor(colorSurface)
        binding.bottomAppBar.setBackgroundColor(colorSurface2)

        setupListeners()
        observeViewModel()
        setupRecyclerView()
        setupDrawerRecyclerView()

    }

    override fun fitSystemBars(statusBarHeight: Int, navigationBarHeight: Int) {
        binding.drawerRecyclerView.updatePadding(top = statusBarHeight)
        binding.toolbarLayout.updatePadding(top = statusBarHeight)
        binding.bottomAppBar.updatePadding(bottom = navigationBarHeight)
    }

    override fun onStart() {
        super.onStart()
        postponeEnterTransition()
    }

    override fun onResume() {
        super.onResume()
        backPressedCallback.isEnabled = true
        binding.bottomAppBar.isVisible = !viewModel.editMode.value.isEditMode &&
                !viewModel.noBrokersYet.value
    }

    override fun onPause() {
        super.onPause()
        backPressedCallback.isEnabled = false
    }

    override fun handleEvent(event: AppEvent): Boolean {
        when (event) {
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
                     .findViewHolderForAdapterPosition(event.position)
                    as TextTileItemViewHolder

                container.navigator().navigate(
                    NavDestination.TextTileDetails,
                    args = bundleOf(NavArg.TILE_ID to event.tileId),
                    extras = FragmentNavigatorExtras(
                        viewHolder.binding.tile to "detailsTile",
                        viewHolder.binding.tileName to "detailsTileName",
                        viewHolder.binding.tilePayload to "detailsPayloadName",
                    )
                )
            }

            is ExportDashboard -> {
                fileExporter.launch(event.fileName)
            }

            is ImportDashboard -> {
                fileImporter.launch(ContentType.JSON)
            }

            is EditModeVibrate -> {
                vibrate()
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
                vibrate()
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

        collectOnStarted(viewModel.drawerItems, drawerListAdapter::submitList)
        collectOnStarted(viewModel.editMode, ::bindEditMode)
        collectOnStarted(viewModel.dashboardName) {
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
            object : ItemMoveCallback.ItemTouchHelperContract {
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
                vibrate()
                viewModel.editModeClicked()
            }
            R.id.delete -> viewModel.deleteTilesClicked()
            R.id.duplicate -> viewModel.duplicateTileClicked()
            R.id.newTile -> viewModel.addTileClicked()
            R.id.selectAll -> viewModel.selectAllClicked()
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

            binding.toolbar.title = viewModel.dashboardName.value
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

    private fun fileForExportSelected(uri: Uri?) {
        uri?.let(viewModel::exportDashboardToFile)
    }

    private fun fileForImportSelected(uri: Uri?) {
        uri?.let(viewModel::importDashboard)
    }

    private fun vibrate() {
        requireContext().getSystemService<Vibrator>()
            ?.vibrate(VibrationEffect.createOneShot(10, 255))
    }
}