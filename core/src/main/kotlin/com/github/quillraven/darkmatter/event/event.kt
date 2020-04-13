package com.github.quillraven.darkmatter.event

import com.badlogic.gdx.utils.Array
import ktx.log.logger
import java.util.*

private val LOG = logger<GameEventManager>()

enum class GameEventType {
    PLAYER_SPAWN,
    PLAYER_DEATH
}

interface GameEventListener {
    fun onEvent(type: GameEventType, data: Any? = null)
}

class GameEventManager {
    private val listeners = EnumMap<GameEventType, Array<GameEventListener>>(GameEventType::class.java)

    fun addListener(type: GameEventType, listener: GameEventListener) {
        var eventListeners = listeners[type]
        if (eventListeners == null) {
            eventListeners = Array(8)
            listeners[type] = eventListeners
        }

        if (listener in eventListeners) {
            LOG.error { "Trying to add already existing listener of type $type: $listener" }
        } else {
            LOG.debug { "Adding listener of type $type: $listener" }
            eventListeners.add(listener)
        }
    }

    fun removeListener(type: GameEventType, listener: GameEventListener) {
        val eventListeners = listeners[type]
        when {
            eventListeners == null -> {
                LOG.error { "Trying to remove listener $listener from non-existing listeners of type $type" }
            }
            listener !in eventListeners -> {
                LOG.error { "Trying to remove non-existing listener of type $type: $listener" }
            }
            else -> {
                LOG.debug { "Removing listener of type $type: $listener" }
                eventListeners.removeValue(listener, true)
            }
        }
    }

    /**
     * This function removes the [listener] from all [types][GameEventType]. It is
     * slightly more efficient to use [removeListener] if you know the exact type(s).
     */
    fun removeListener(listener: GameEventListener) {
        LOG.debug { "Removing $listener from all types" }
        listeners.values.forEach { it.removeValue(listener, true) }
    }

    fun dispatchEvent(type: GameEventType, data: Any? = null) {
        LOG.debug { "Dispatch event $type with data: $data" }
        listeners[type]?.forEach { it.onEvent(type, data) }
    }
}
