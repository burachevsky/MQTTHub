package com.github.burachevsky.mqtthub.feature.home

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.container.ViewContainer
import com.github.burachevsky.mqtthub.common.container.ViewController
import com.github.burachevsky.mqtthub.common.effect.EffectHandler
import com.github.burachevsky.mqtthub.common.effect.UIEffect
import com.github.burachevsky.mqtthub.common.ext.appComponent
import com.github.burachevsky.mqtthub.common.ext.collectOnStarted
import com.github.burachevsky.mqtthub.common.recycler.CompositeAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemMoveCallback
import com.github.burachevsky.mqtthub.databinding.FragmentHomeBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.github.burachevsky.mqtthub.feature.home.item.*
import timber.log.Timber
import javax.inject.Inject

class HomeFragment : Fragment(), ViewController<HomeViewModel>, EffectHandler {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override val container = ViewContainer(this, ::HomeNavigator)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<HomeViewModel>

    override lateinit var viewModel: HomeViewModel

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
        DrawerLabelItemAdapter(),
        DrawerMenuItemAdapter(
            object : DrawerMenuItem.Listener {
                override fun onClick(position: Int) {
                    viewModel.drawerManager.onMenuItemClick(position)
                }
            }
        ),
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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupListeners()
        observeViewModel()
        setupRecyclerView()
        setupDrawerRecyclerView()
    }

    override fun handleEffect(effect: UIEffect): Boolean {
        when (effect) {
            is CloseHomeDrawer -> {
                binding.drawerLayout.close()
                return true
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

            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            adapter = listAdapter
        }

        val moveCallback = makeItemMoveCallback()
        val touchHelper = ItemTouchHelper(moveCallback)
        touchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun setupDrawerRecyclerView() {
        binding.drawerRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), VERTICAL, false)
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

        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        viewModel.navigateUp()
                    }
                }
            )
    }

    private fun observeViewModel() {
        collectOnStarted(viewModel.connectionState, binding.connection::applyState)
        collectOnStarted(viewModel.items, listAdapter::submitList)
        collectOnStarted(viewModel.drawerManager.items, drawerListAdapter::submitList)
        collectOnStarted(viewModel.editMode, ::bindEditMode)
        collectOnStarted(viewModel.title, binding.toolbar::setTitle)

        collectOnStarted(viewModel.noTilesYet) {
            binding.noTilesText.isVisible = it
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