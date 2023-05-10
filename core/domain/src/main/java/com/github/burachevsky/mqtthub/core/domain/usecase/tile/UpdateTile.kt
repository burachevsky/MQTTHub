package com.github.burachevsky.mqtthub.core.domain.usecase.tile

import com.github.burachevsky.mqtthub.core.data.repository.TileRepository
import com.github.burachevsky.mqtthub.core.model.Tile
import javax.inject.Inject

class UpdateTile @Inject constructor(
    private val tileRepository: TileRepository
) {
    suspend operator fun invoke(tile: Tile) {
        return tileRepository.updateTile(tile)
    }
}