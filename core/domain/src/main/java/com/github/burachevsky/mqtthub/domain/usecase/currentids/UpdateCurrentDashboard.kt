package com.github.burachevsky.mqtthub.domain.usecase.currentids

import com.github.burachevsky.mqtthub.core.data.repository.CurrentIdsRepository
import javax.inject.Inject

class UpdateCurrentDashboard @Inject constructor(
    private val currentIdsRepository: CurrentIdsRepository
) {

    suspend operator fun invoke(dashboardId: Long) {
        return currentIdsRepository.updateCurrentDashboard(dashboardId)
    }
}