package com.github.burachevsky.mqtthub.core.domain.usecase.dashboard

import com.github.burachevsky.mqtthub.core.data.repository.DashboardRepository
import com.github.burachevsky.mqtthub.core.model.Dashboard
import javax.inject.Inject

class GetDashboards @Inject constructor(
    private val dashboardRepository: DashboardRepository
) {

    suspend operator fun invoke(): List<Dashboard> {
        return dashboardRepository.getDashboards()
    }
}