package com.github.burachevsky.mqtthub.feature.tiledetails.text

import androidx.lifecycle.ViewModel
import com.github.burachevsky.mqtthub.common.container.VM
import com.github.burachevsky.mqtthub.common.container.viewModelContainer
import com.github.burachevsky.mqtthub.common.navigation.Navigator
import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.domain.eventbus.EventBus
import com.github.burachevsky.mqtthub.domain.usecase.tile.ObserveTile
import com.github.burachevsky.mqtthub.feature.publishtext.PublishTextEntered
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TextTileDetailsViewModel @Inject constructor(
    private val tileId: Long,
    observeTile: ObserveTile,
    private val eventBus: EventBus,
) : ViewModel(), VM<Navigator> {

    override val container = viewModelContainer()

    private val tile: Flow<Tile> = observeTile(tileId)

    val tileName: Flow<String> = tile.map { it.name }
    val tilePayload: Flow<String> = tile.map { it.payload }
    val isSendEnabled: Flow<Boolean> = tile.map { it.publishTopic.isNotEmpty() }
        .distinctUntilChanged()

    fun enterPublishText(text: String) {
        container.launch(Dispatchers.Default) {
            eventBus.send(PublishTextEntered(tileId, text))
        }
    }
}