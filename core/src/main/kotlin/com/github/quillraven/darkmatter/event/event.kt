package com.github.quillraven.darkmatter.event

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.ObjectMap
import com.github.quillraven.darkmatter.ecs.component.PowerUpType
import ktx.collections.GdxSet
import ktx.log.debug
import ktx.log.error
import ktx.log.logger
import kotlin.reflect.KClass

private val LOG = logger<GameEventManager>()
private const val INITIAL_LISTENER_CAPACITY = 8

sealed class GameEvent {
    object PlayerSpawn : GameEvent()
    object PlayerDeath : GameEvent() {
        var distance = 0f
    }

    object PlayerBlock : GameEvent() {
        var shield = 0f
        var maxShield = 0f
    }

    object PlayerMove : GameEvent() {
        var distance = 0f
        var speed = 0f
    }

    object PlayerHit : GameEvent() {
        lateinit var player: Entity
        var life = 0f
        var maxLife = 0f
    }

    object PowerUp : GameEvent() {
        lateinit var player: Entity
        var type = PowerUpType.NONE
    }
}

interface GameEventListener {
    fun onEvent(event: GameEvent)
}

class GameEventManager {
    private val listeners = ObjectMap<KClass<out GameEvent>, GdxSet<GameEventListener>>()

    fun addListener(type: KClass<out GameEvent>, listener: GameEventListener) {
        var eventListeners = listeners[type]
        if (eventListeners == null) {
            eventListeners = GdxSet(INITIAL_LISTENER_CAPACITY)
            listeners.put(type, eventListeners)
        }

        if (eventListeners.add(listener)) {
            LOG.debug { "Adding listener of type $type: $listener" }
        } else {
            LOG.error { "Trying to add already existing listener of type $type: $listener" }
        }
    }

    fun removeListener(type: KClass<out GameEvent>, listener: GameEventListener) {
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
                eventListeners.remove(listener)
            }
        }
    }

    /**
     * This function removes the [listener] from all [types][GameEvent]. It is
     * slightly more efficient to use [removeListener] if you know the exact type(s).
     */
    fun removeListener(listener: GameEventListener) {
        LOG.debug { "Removing $listener from all types" }
        listeners.values().forEach { it.remove(listener) }
    }

    fun dispatchEvent(event: GameEvent) {
        LOG.debug { "Dispatch event $event" }
        listeners[event::class]?.forEach { it.onEvent(event) }
    }
}
