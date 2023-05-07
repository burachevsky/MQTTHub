package com.github.burachevsky.mqtthub.core.ui.recycler

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemMoveCallback(
    private val adapter: ItemTouchHelperContract
) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {}

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = if (adapter.canMove()) {
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        } else 0

        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onItemMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSelectedChanged(
        viewHolder: RecyclerView.ViewHolder?,
        actionState: Int
    ) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder is ItemViewHolder) {
                adapter.onItemSelected(viewHolder.adapterPosition)
            }
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) {
        super.clearView(recyclerView, viewHolder)
        if (viewHolder is ItemViewHolder) {
            adapter.onItemReleased(viewHolder.adapterPosition)
        }
    }

    interface ItemTouchHelperContract {
        fun onItemMoved(fromPosition: Int, toPosition: Int)
        fun onItemSelected(position: Int)
        fun onItemReleased(position: Int)
        fun canMove(): Boolean
    }
}
