package com.github.burachevsky.mqtthub.feature.home

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.burachevsky.mqtthub.core.ui.R

class TileLayoutDecoration(
    context: Context
) : RecyclerView.ItemDecoration() {

    private val spacing = context.resources.getDimensionPixelSize(R.dimen.dashboard_tile_spacing)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            left = spacing
            right = spacing
            top = spacing
            bottom = spacing
        }
    }
}