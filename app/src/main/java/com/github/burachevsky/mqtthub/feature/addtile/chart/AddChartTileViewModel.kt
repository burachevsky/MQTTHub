package com.github.burachevsky.mqtthub.feature.addtile.chart

import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of
import com.github.burachevsky.mqtthub.common.widget.ToggleGroupItem
import com.github.burachevsky.mqtthub.common.widget.ToggleOption
import com.github.burachevsky.mqtthub.data.entity.ChartTileStyleId
import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.domain.eventbus.EventBus
import com.github.burachevsky.mqtthub.domain.usecase.tile.AddTile
import com.github.burachevsky.mqtthub.domain.usecase.tile.GetTile
import com.github.burachevsky.mqtthub.domain.usecase.tile.UpdateTile
import com.github.burachevsky.mqtthub.feature.addtile.AddTileViewModel
import com.github.burachevsky.mqtthub.feature.addtile.DASHBOARD_ID
import com.github.burachevsky.mqtthub.feature.addtile.DASHBOARD_POSITION
import com.github.burachevsky.mqtthub.feature.addtile.TILE_ID
import javax.inject.Inject
import javax.inject.Named

class AddChartTileViewModel@Inject constructor(
    @Named(DASHBOARD_ID) dashboardId: Long,
    @Named(TILE_ID) tileId: Long,
    @Named(DASHBOARD_POSITION) dashboardPosition: Int,
    eventBus: EventBus,
    getTile: GetTile,
    updateTile: UpdateTile,
    addTile: AddTile,
) : AddTileViewModel(eventBus, getTile, updateTile, addTile, dashboardId, tileId, dashboardPosition) {

    override val title: Int = if (isEditMode()) R.string.edit_chart else R.string.new_chart

    private val style = ToggleGroupItem(
        title = Txt.of(R.string.tile_background_style),
        options = listOf(
            ToggleOption(
                id = ChartTileStyleId.OUTLINED,
                text = Txt.of(R.string.tile_style_outlined),
            ),
            ToggleOption(
                id = ChartTileStyleId.FILLED,
                text = Txt.of(R.string.tile_style_filled),
            ),
            ToggleOption(
                id = ChartTileStyleId.EMPTY,
                text = Txt.of(R.string.tile_style_empty),
            ),
        ),
        selectedValue = ChartTileStyleId.OUTLINED,
    )

    init {
        init()
    }

    override fun initFields(tile: Tile) {
        name.text = tile.name
        subscribeTopic.text = tile.subscribeTopic
        retain.isChecked = tile.retained
        qos.selectedValue = tile.qos
        style.selectedValue = tile.design.styleId
    }

    override fun list(): List<ListItem> {
        return listOf(
            name,
            subscribeTopic,
            retain,
            qos,
            style,
            save,
        )
    }

    override fun collectTile(): Tile {
        return oldTile?.copy(
            name = name.text,
            subscribeTopic = subscribeTopic.text,
            qos = qos.selectedValue,
            retained = retain.isChecked,
            dashboardId = dashboardId,
            type = Tile.Type.CHART,
            stateList = emptyList(),
            design = Tile.Design(
                styleId = style.selectedValue,
                isFullSpan = true,
            ),
        ) ?: Tile(
            name = name.text,
            subscribeTopic = subscribeTopic.text,
            publishTopic = "",
            qos = qos.selectedValue,
            retained = retain.isChecked,
            dashboardId = dashboardId,
            type = Tile.Type.CHART,
            stateList = emptyList(),
            dashboardPosition = dashboardPosition,
            design = Tile.Design(
                styleId = style.selectedValue,
                isFullSpan = true,
            ),
        )
    }
}