package com.github.burachevsky.mqtthub.core.database.entity.tile

sealed class TopicUpdate {
    abstract val set: Set<String>
    
    data class TopicsAdded(override val set: Set<String>) : TopicUpdate()
    data class TopicsRemoved(override val set: Set<String>) : TopicUpdate()
}