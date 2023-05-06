package com.github.burachevsky.mqtthub.domain.usecase.tile

import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.data.repository.TileRepository
import javax.inject.Inject

class UpdatePayloadAndGetTilesToNotify @Inject constructor(
    private val tileRepository: TileRepository
) {

    suspend operator fun invoke(subscribeTopic: String, payload: String): List<Tile> {
        return tileRepository.updatePayloadAndGetTilesToNotify(subscribeTopic, payload)
    }
}