package com.github.burachevsky.mqtthub.feature.home

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.burachevsky.mqtthub.R

class TileLayoutDecoration(
    context: Context
) : RecyclerView.ItemDecoration() {

    private val margin = context.resources.getDimensionPixelSize(R.dimen.dashboard_margin)
    private val spacing = context.resources.getDimensionPixelSize(R.dimen.dashboard_tile_spacing)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            val pos = parent.getChildAdapterPosition(view)

            if (pos % 2 == 0) {
                left = margin
                right = spacing
            } else {
                right = margin
            }

            top = spacing
        }
    }
}