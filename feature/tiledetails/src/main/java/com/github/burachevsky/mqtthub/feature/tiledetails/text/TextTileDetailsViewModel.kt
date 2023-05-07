package com.github.burachevsky.mqtthub.feature.tiledetails.text

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.burachevsky.mqtthub.domain.usecase.tile.ObserveTile
import com.github.burachevsky.mqtthub.core.database.entity.tile.Tile
import com.github.burachevsky.mqtthub.core.eventbus.EventBus
import com.github.burachevsky.mqtthub.core.ui.R
import com.github.burachevsky.mqtthub.core.ui.container.VM
import com.github.burachevsky.mqtthub.core.ui.container.viewModelContainer
import com.github.burachevsky.mqtthub.core.ui.dialog.entertext.EnterTextActionId
import com.github.burachevsky.mqtthub.core.ui.dialog.entertext.TextEntered
import com.github.burachevsky.mqtthub.core.ui.event.PublishTextEntered
import com.github.burachevsky.mqtthub.core.ui.text.Txt
import com.github.burachevsky.mqtthub.core.ui.text.of
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TextTileDetailsViewModel @Inject constructor(
    private val tileId: Long,
    observeTile: ObserveTile,
    private val eventBus: EventBus,
) : ViewModel(), VM<TextTileDetailsNavigator> {

    override val container = viewModelContainer()

    private val tile: Flow<Tile> = observeTile(tileId)

    val tileName: Flow<String> = tile.map { it.name }
    val tilePayload: Flow<String> = tile.map { it.payload }
    val isSendEnabled: Flow<Boolean> = tile.map { it.publishTopic.isNotEmpty() }
        .distinctUntilChanged()

    init {
        container.launch(Dispatchers.Main) {
            eventBus.subscribe<TextEntered>(viewModelScope) {
                if (it.actionId == EnterTextActionId.TEXT_TILE_PUBLISH) {
                    publishPayload(it.text)
                }
            }
        }
    }

    fun publishTextClicked() {
        container.navigator {
            navigateEnterText(
                actionId = EnterTextActionId.TEXT_TILE_PUBLISH,
                title = Txt.of(R.string.tile_details_publish)
            )
        }
    }

    private fun publishPayload(payload: String) {
        container.launch(Dispatchers.Default) {
            eventBus.send(PublishTextEntered(tileId, payload))
        }
    }
}