package com.github.burachevsky.mqtthub.domain.usecase.dashboard

import com.github.burachevsky.mqtthub.data.entity.Dashboard
import com.github.burachevsky.mqtthub.data.repository.DashboardRepository
import javax.inject.Inject

class AddDashboard @Inject constructor(
    private val dashboardRepository: DashboardRepository
) {

    suspend operator fun invoke(dashboard: Dashboard): Dashboard {
        return dashboardRepository.insertDashboard(dashboard)
    }
}