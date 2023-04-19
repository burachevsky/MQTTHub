package com.github.burachevsky.mqtthub.domain.usecase.tile

import com.github.burachevsky.mqtthub.data.entity.Tile
import com.github.burachevsky.mqtthub.data.repository.TileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTile @Inject constructor(
    private val tileRepository: TileRepository,
) {

    operator fun invoke(tileId: Long): Flow<Tile> {
        return tileRepository.observeTile(tileId)
    }
}