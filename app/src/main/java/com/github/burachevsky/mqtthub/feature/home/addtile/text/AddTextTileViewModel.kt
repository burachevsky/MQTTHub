package com.github.burachevsky.mqtthub.feature.home.addtile.text

import com.github.burachevsky.mqtthub.R
import com.github.burachevsky.mqtthub.domain.eventbus.EventBus
import com.github.burachevsky.mqtthub.common.recycler.ListItem
import com.github.burachevsky.mqtthub.common.text.Txt
import com.github.burachevsky.mqtthub.common.text.of
import com.github.burachevsky.mqtthub.common.widget.SwitchItem
import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.domain.usecase.tile.AddTile
import com.github.burachevsky.mqtthub.domain.usecase.tile.GetTile
import com.github.burachevsky.mqtthub.domain.usecase.tile.UpdateTile
import com.github.burachevsky.mqtthub.feature.home.addtile.AddTileViewModel
import com.github.burachevsky.mqtthub.feature.home.addtile.DASHBOARD_ID
import com.github.burachevsky.mqtthub.feature.home.addtile.DASHBOARD_POSITION
import com.github.burachevsky.mqtthub.feature.home.addtile.TILE_ID
import javax.inject.Inject
import javax.inject.Named

class AddTextTileViewModel @Inject constructor(
    @Named(DASHBOARD_ID) dashboardId: Long,
    @Named(TILE_ID) tileId: Long,
    @Named(DASHBOARD_POSITION) dashboardPosition: Int,
    eventBus: EventBus,
    addTile: AddTile,
    updateTile: UpdateTile,
    getTile: GetTile,
) : AddTileViewModel(eventBus, getTile, updateTile, addTile, dashboardId, tileId, dashboardPosition) {

    override val title: Int = if (isEditMode()) R.string.edit_text_tile else R.string.new_text_tile

    private val enablePublishing = SwitchItem(
        text = Txt.of(R.string.enable_publishing),
        onCheckChanged = { showPublishingField() }
    )

    init {
        init()
    }

    private fun update() {
        _items.value = list()
    }

    private fun showPublishingField() {
        if (publishTopic.text.isEmpty()) {
            publishTopic.text = subscribeTopic.text
        }

        update()
    }

    override fun initFields(tile: Tile) {
        name.text = tile.name
        subscribeTopic.text = tile.subscribeTopic
        enablePublishing.isChecked = tile.publishTopic.isNotEmpty()
        publishTopic.text = tile.publishTopic
        retain.isChecked = tile.retained
        qos.selectedValue = tile.qos
    }

    override fun collectTile(): Tile {
        return oldTile?.copy(
            name = name.text,
            subscribeTopic = subscribeTopic.text,
            publishTopic = if (enablePublishing.isChecked) publishTopic.text else "",
            qos = qos.selectedValue,
            retained = retain.isChecked,
            dashboardId = dashboardId,
            type = Tile.Type.TEXT,
            stateList = emptyList()
        ) ?: Tile(
            name = name.text,
            subscribeTopic = subscribeTopic.text,
            publishTopic = if (enablePublishing.isChecked) publishTopic.text else "",
            qos = qos.selectedValue,
            retained = retain.isChecked,
            dashboardId = dashboardId,
            type = Tile.Type.TEXT,
            stateList = emptyList(),
            dashboardPosition = dashboardPosition,
        )
    }

    override fun list(): List<ListItem> {
        return listOfNotNull(
            name,
            subscribeTopic,
            if (enablePublishing.isChecked) publishTopic else null,
            enablePublishing,
            qos,
            retain,
            save,
        )
    }
}