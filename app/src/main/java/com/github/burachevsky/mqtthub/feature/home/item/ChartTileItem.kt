package com.github.burachevsky.mqtthub.feature.home.item

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.common.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.data.entity.ChartTileStyleId
import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.databinding.ListItemChartTileBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

data class ChartTileItem(
    override val tile: Tile,
    override val editMode: EditMode? = null
) : TileItem {

    override fun layout() = LAYOUT

    override fun getChangePayload(that: ListItem): List<Int> {
        that as ChartTileItem

        return listOfNotNull(
            if (tile.name != that.tile.name) NAME_CHANGED else null,
            if (tile.payload != that.tile.payload) PAYLOAD_CHANGED else null,
            if (editMode != that.editMode) EDIT_MODE_CHANGED else null,
            if (tile.design != that.tile.design) DESIGN_CHANGED else null,
        )
    }

    override fun copyTile(tile: Tile): TileItem {
        return copy(tile = tile)
    }

    override fun withEditMode(editMode: EditMode?): TileItem {
        return copy(editMode = editMode)
    }

    companion object {
        val LAYOUT get() = R.layout.list_item_chart_tile
    }
}

class ChartTileItemViewHolder(
    itemView: View,
    private val listener: TileItem.Listener
): ItemViewHolder(itemView) {

    private val binding = ListItemChartTileBinding.bind(itemView)

    init {
        binding.tile.setOnClickListener {
            listener.onClick(adapterPosition)
        }

        binding.editModeOverlay.setOnClickListener {
            listener.onClick(adapterPosition)
        }

        binding.editModeOverlay.setOnLongClickListener {
            listener.onLongClick(adapterPosition)
        }
    }

    override fun bind(item: ListItem, payloads: List<Int>) {
        item as ChartTileItem

        payloads.forEach {
            when (it) {
                NAME_CHANGED -> bindTileName(item)
                PAYLOAD_CHANGED -> bindTilePayload(item)
                EDIT_MODE_CHANGED -> bindEditMode(item.editMode)
                DESIGN_CHANGED -> bindDesign(item)
            }
        }
    }

    override fun bind(item: ListItem) {
        item as ChartTileItem

        bindTileName(item)
        bindDesign(item)
        bindTilePayload(item)
        bindEditMode(item.editMode)
    }

    private fun bindTileName(item: ChartTileItem) {
        binding.tileName.text = item.tile.name
    }

    private fun bindDesign(item: ChartTileItem) {
        binding.tile.apply {
            setBackgroundResource(
                when (item.tile.design.styleId){
                    ChartTileStyleId.FILLED -> R.drawable.bg_tile_list_item_filled
                    ChartTileStyleId.OUTLINED -> R.drawable.bg_tile_list_item_outlined
                    else -> R.drawable.bg_tile_list_item_empty
                }
            )

            layoutParams = StaggeredGridLayoutManager.LayoutParams(layoutParams).apply {
                isFullSpan = true
            }
        }
    }

    private fun bindTilePayload(item: ChartTileItem) {
        val payload = item.tile.chartPayload ?: return

        val entries = payload.data.mapIndexed { i, it ->
            Entry(i.toFloat(), it.y)
        }

        val primaryColor = context.getColor(R.color.md_theme_light_primary)

        val dataSet = LineDataSet(entries, null).apply {
            lineWidth = 2f
            color = primaryColor
            setCircleColor(primaryColor)
            setDrawCircleHole(false)
            setDrawValues(false)
        }
        binding.chart.apply {
            legend.isEnabled = false
            setTouchEnabled(false)
            axisRight.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawAxisLine(false)
            xAxis.setDrawGridLines(true)
            xAxis.valueFormatter = IndexAxisValueFormatter(payload.data.map { it.x })
            data = LineData(dataSet)
            description.isEnabled = false
            invalidate()
        }
    }
}

class ChartTileItemAdapter(
    private val listener: TileItem.Listener
) : ItemAdapter {

    override fun viewType() = ChartTileItem.LAYOUT

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        return ChartTileItemViewHolder(inflateItemView(parent), listener)
    }
}