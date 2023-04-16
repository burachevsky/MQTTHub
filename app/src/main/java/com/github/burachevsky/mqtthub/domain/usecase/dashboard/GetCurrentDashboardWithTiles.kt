package com.github.burachevsky.mqtthub.domain.usecase.dashboard

import com.github.burachevsky.mqtthub.data.entity.DashboardWithTiles
import com.github.burachevsky.mqtthub.data.repository.DashboardRepository
import javax.inject.Inject

class GetCurrentDashboardWithTiles @Inject constructor(
    private val dashboardRepository: DashboardRepository
) {

    suspend operator fun invoke(): DashboardWithTiles {
        return dashboardRepository.getCurrentDashboardWithTiles()
    }
}