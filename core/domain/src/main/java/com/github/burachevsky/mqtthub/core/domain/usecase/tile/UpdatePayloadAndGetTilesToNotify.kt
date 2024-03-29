package com.github.burachevsky.mqtthub.core.domain.usecase.tile

import com.github.burachevsky.mqtthub.core.data.repository.TileRepository
import com.github.burachevsky.mqtthub.core.model.Tile
import javax.inject.Inject

class UpdatePayloadAndGetTilesToNotify @Inject constructor(
    private val tileRepository: TileRepository
) {

    suspend operator fun invoke(subscribeTopic: String, payload: String): List<Tile> {
        return tileRepository.updatePayloadAndGetTilesToNotify(subscribeTopic, payload)
    }
}