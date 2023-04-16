package com.github.burachevsky.mqtthub.domain.usecase.dashboard

import com.github.burachevsky.mqtthub.data.entity.Dashboard
import com.github.burachevsky.mqtthub.data.repository.DashboardRepository
import javax.inject.Inject

class GetDashboards @Inject constructor(
    private val dashboardRepository: DashboardRepository
) {

    suspend operator fun invoke(): List<Dashboard> {
        return dashboardRepository.getDashboards()
    }
}