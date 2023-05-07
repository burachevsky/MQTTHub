package com.github.burachevsky.mqtthub.feature.home.item.tile

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.burachevsky.mqtthub.core.model.ChartPayload
import com.github.burachevsky.mqtthub.core.model.Tile
import com.github.burachevsky.mqtthub.core.ui.ext.getValueFromAttribute
import com.github.burachevsky.mqtthub.core.ui.recycler.ItemAdapter
import com.github.burachevsky.mqtthub.core.ui.recycler.ItemViewHolder
import com.github.burachevsky.mqtthub.core.ui.recycler.ListItem
import com.github.burachevsky.mqtthub.feature.home.databinding.ListItemChartTileBinding
import com.github.burachevsky.mqtthub.feature.home.item.APPEARANCE_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.EditMode
import com.github.burachevsky.mqtthub.feature.home.item.NAME_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.PAYLOAD_CHANGED
import com.github.burachevsky.mqtthub.feature.home.item.TileItem
import com.github.burachevsky.mqtthub.feature.home.item.bindEditModeAndBackground
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.burachevsky.mqtthub.feature.home.R as featureR

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
            if (editMode != that.editMode || tile.design != that.tile.design)
                APPEARANCE_CHANGED else null,
        )
    }

    override fun copyTile(tile: Tile): TileItem {
        return copy(tile = tile)
    }

    override fun withEditMode(editMode: EditMode?): TileItem {
        return copy(editMode = editMode)
    }

    companion object {
        val LAYOUT get() = featureR.layout.list_item_chart_tile
    }
}

class ChartTileItemViewHolder(
    itemView: View,
    private val listener: TileItem.Listener
): ItemViewHolder(itemView) {

    private val binding = ListItemChartTileBinding.bind(itemView)

    private val primaryColor = context
        .getValueFromAttribute(com.google.android.material.R.attr.colorPrimary)

    private val textColor = context
        .getValueFromAttribute(com.google.android.material.R.attr.colorOnSurface)

    private val outlineColor = context
        .getValueFromAttribute(com.google.android.material.R.attr.colorOutline)

    init {
        binding.tile.setOnLongClickListener {
            listener.onLongClick(adapterPosition)
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
                APPEARANCE_CHANGED -> bindAppearance(item)
            }
        }
    }

    override fun bind(item: ListItem) {
        item as ChartTileItem

        bindTileName(item)
        bindTilePayload(item)
        bindAppearance(item)
    }

    private fun bindTileName(item: ChartTileItem) {
        binding.tileName.text = item.tile.name
    }

    private fun bindAppearance(item: ChartTileItem) {
        binding.tile.apply {
            layoutParams = StaggeredGridLayoutManager.LayoutParams(layoutParams).apply {
                isFullSpan = true
            }

            bindEditModeAndBackground(item)
        }
    }

    private fun bindTilePayload(item: ChartTileItem) {
        val payload = item.tile.payload
        if (payload !is ChartPayload) return

        binding.yAxisTitle.text = payload.yTitle

        val entries = payload.data.mapIndexed { i, it ->
            Entry(i.toFloat(), it.y)
        }

        val dataSet = LineDataSet(entries, null).apply {
            lineWidth = 2f
            color = primaryColor
            setCircleColor(primaryColor)
            setDrawCircleHole(false)
            setDrawValues(false)
            valueTextColor = textColor
        }
        binding.chart.apply {
            axisLeft.setDrawAxisLine(false)
            axisLeft.setDrawGridLines(true)
            axisLeft.textColor = textColor
            axisRight.isEnabled = false
            axisLeft.axisLineColor = outlineColor
            axisLeft.gridColor = outlineColor

            xAxis.valueFormatter = IndexAxisValueFormatter(payload.data.map { it.x })
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawAxisLine(false)
            xAxis.setDrawGridLines(true)
            xAxis.textColor = textColor
            xAxis.gridColor = outlineColor

            legend.isEnabled = false
            setTouchEnabled(false)
            data = LineData(dataSet)
            description.isEnabled = false
            setNoDataTextColor(textColor)

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