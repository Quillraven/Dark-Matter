package com.github.quillraven.darkmatter.event

import com.badlogic.gdx.utils.Array
import ktx.log.logger

private val LOG = logger<GameEventManager>()

enum class EventType {
    PLAYER_SPAWN,
    PLAYER_DEATH
}

interface GameEventListener {
    fun onEvent(type: EventType, data: Any? = null)
}

class GameEventManager {
    private val listeners = Array<GameEventListener>(8)

    fun addListener(listener: GameEventListener) {
        if (listener in listeners) {
            LOG.error { "Trying to add already existing listener: $listener" }
            return
        } else {
            LOG.debug { "Adding listener: $listener" }
        }
        listeners.add(listener)
    }

    fun removeListener(listener: GameEventListener) {
        if (listener in listeners) {
            LOG.error { "Trying to remove non-existing listener: $listener" }
            return
        } else {
            LOG.debug { "Removing listener: $listener" }
        }
        listeners.removeValue(listener, true)
    }

    fun dispatchEvent(type: EventType, data: Any? = null) {
        LOG.debug { "Dispatch event $type with data: $data" }
        listeners.forEach { it.onEvent(type, data) }
    }
}
