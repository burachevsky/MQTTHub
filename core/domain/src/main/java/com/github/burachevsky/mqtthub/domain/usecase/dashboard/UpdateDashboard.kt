package com.github.burachevsky.mqtthub.domain.usecase.dashboard

import com.github.burachevsky.mqtthub.core.data.repository.DashboardRepository
import com.github.burachevsky.mqtthub.core.model.Dashboard
import javax.inject.Inject

class UpdateDashboard @Inject constructor(
    private val dashboardRepository: DashboardRepository,
) {

    suspend operator fun invoke(dashboard: Dashboard) {
        return dashboardRepository.updateDashboard(dashboard)
    }
}