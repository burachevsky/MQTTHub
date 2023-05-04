package com.github.burachevsky.mqtthub.domain.usecase.dashboard

import com.github.burachevsky.mqtthub.data.repository.DashboardRepository
import javax.inject.Inject

class UpdateDashboardName @Inject constructor(
    private val dashboardRepository: DashboardRepository
) {

    suspend operator fun invoke(dashboardId: Long, name: String) {
        dashboardRepository.updateDashboardName(dashboardId, name)
    }
}