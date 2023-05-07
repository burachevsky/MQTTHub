package com.github.burachevsky.mqtthub.core.data.repository.impl.cache

import com.github.burachevsky.mqtthub.core.database.entity.tile.Tile
import com.github.burachevsky.mqtthub.core.database.entity.tile.TopicUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TileMemoryCache @Inject constructor() {

    private val cache = ArrayList<Tile>()

    private val cacheMutex = Mutex()

    private val subscriptionTopicsSet = MutableSharedFlow<Set<String>>(replay = 1)

    fun observeTopicUpdates(): Flow<TopicUpdate> {
        return flow {
            var previousSet = emptySet<String>()

            subscriptionTopicsSet.collect { set ->
                val addedTopics = set.minus(previousSet)
                if (addedTopics.isNotEmpty()) {
                    emit(TopicUpdate.TopicsAdded(addedTopics))
                }

                val removedTopics = previousSet.minus(set)
                if (removedTopics.isNotEmpty()) {
                    emit(TopicUpdate.TopicsRemoved(removedTopics))
                }

                previousSet = set
            }
        }
    }

    private var isInitialized = false

    private suspend fun <T> withLock(action: suspend (ArrayList<Tile>) -> T): T {
        return cacheMutex.withLock {
            withContext(Dispatchers.Default) {
                action(cache)
            }
        }
    }

    suspend fun <T> apply(
        initializer: suspend () -> List<Tile>,
        action: suspend (TileMemoryCache).() -> T
    ): T {
        return withLock {
            if (!isInitialized) {
                cache.clear()
                cache.addAll(initializer())
                updateSubscriptionList()
                isInitialized = true
            }

            action()
        }
    }

    suspend fun insert(tile: Tile) {
        cache.add(tile)
        updateSubscriptionList()
    }

    suspend fun update(tile: Tile) {
        update(listOf(tile))
    }

    suspend fun update(tiles: List<Tile>) {
        val updatedTilesById = tiles.associateBy { it.id }

        for (i in cache.indices) {
            cache[i] = updatedTilesById[cache[i].id] ?: continue
        }

        updateSubscriptionList()
    }

    suspend fun delete(id: Long) {
        delete(listOf(id))
    }

    suspend fun delete(ids: List<Long>) {
        val idsSet = ids.toSet()
        cache.removeAll { idsSet.contains(it.id) }
        updateSubscriptionList()
    }

    fun updatePayloadAndGetTilesToNotify(
        subscribeTopic: String,
        payload: String
    ): List<Tile> {

        val result = ArrayList<Tile>()

        for (i in cache.indices) {
            val current = cache[i]

            if (current.subscribeTopic == subscribeTopic) {
                val updated = current.copy(payload = payload)
                cache[i] = updated

                if (updated.notifyPayloadUpdate) {
                    result.add(updated)
                }
            }
        }

        return result
    }

    fun getDashboardTiles(dashboardId: Long): List<Tile> {
        return cache.filter { it.dashboardId == dashboardId }
            .sortedBy { it.dashboardPosition }
    }

    fun getById(id: Long): Tile {
        return cache.find { it.id == id }!!
    }

    fun getAll(): List<Tile> {
        return cache.toList()
    }

    private suspend fun updateSubscriptionList() {
        subscriptionTopicsSet.emit(
            cache.map { it.subscribeTopic }.toSet()
        )
    }
}