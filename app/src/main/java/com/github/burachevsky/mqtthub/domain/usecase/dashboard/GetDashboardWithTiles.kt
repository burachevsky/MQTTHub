package com.github.burachevsky.mqtthub.domain.usecase.dashboard

import com.github.burachevsky.mqtthub.data.entity.DashboardWithTiles
import com.github.burachevsky.mqtthub.data.repository.DashboardRepository
import javax.inject.Inject

class GetDashboardWithTiles @Inject constructor(
    private val dashboardRepository: DashboardRepository
) {

    suspend operator fun invoke(dashboardId: Long): DashboardWithTiles {
        return dashboardRepository.getDashboardWithTiles(dashboardId)
    }
}