package com.github.burachevsky.mqtthub.core.domain.usecase.dashboard

import com.github.burachevsky.mqtthub.core.data.repository.CurrentIdsRepository
import com.github.burachevsky.mqtthub.core.data.repository.DashboardRepository
import com.github.burachevsky.mqtthub.core.model.Dashboard
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class ObserveCurrentDashboard @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val currentIdsRepository: CurrentIdsRepository,
) {

    operator fun invoke(): Flow<Dashboard> {
        return channelFlow {
            coroutineScope {
                var job: Job? = null
                var currentId: Long

                currentIdsRepository
                    .observeCurrentDashboardId()
                    .distinctUntilChanged()
                    .collect { id ->
                        job?.cancel()

                        currentId = id ?: dashboardRepository
                            .insertDashboard(createFirstDashboard()).id

                        currentIdsRepository.updateCurrentDashboard(currentId)

                        job = launch {
                            dashboardRepository.observeDashboard(currentId)
                                .filterNotNull()
                                .onEach { dashboard ->
                                    if (isActive && id == currentId) {
                                        send(dashboard)
                                    }
                                }
                                .collect()
                        }
                    }
            }
        }
    }

    private fun createFirstDashboard(): Dashboard {
        return Dashboard(name = "Dashboard")
    }
}