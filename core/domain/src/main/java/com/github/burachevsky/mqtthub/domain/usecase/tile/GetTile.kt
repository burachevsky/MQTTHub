package com.github.burachevsky.mqtthub.domain.usecase.tile

import com.github.burachevsky.mqtthub.core.data.repository.TileRepository
import com.github.burachevsky.mqtthub.core.database.entity.tile.Tile
import javax.inject.Inject

class GetTile @Inject constructor(
    private val tileRepository: TileRepository
) {
    suspend operator fun invoke(tileId: Long): Tile {
        return tileRepository.getTile(tileId)
    }
}