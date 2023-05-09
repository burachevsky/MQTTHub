package com.github.burachevsky.mqtthub.core.domain.usecase.tile

import com.github.burachevsky.mqtthub.core.data.repository.TileRepository
import javax.inject.Inject

class DeleteTile @Inject constructor(
    private val tileRepository: TileRepository
) {
    suspend operator fun invoke(id: Long) {
        return tileRepository.deleteTile(id)
    }
}