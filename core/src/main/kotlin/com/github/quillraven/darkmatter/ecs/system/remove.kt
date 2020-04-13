package com.github.quillraven.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.github.quillraven.darkmatter.ecs.component.PlayerComponent
import com.github.quillraven.darkmatter.ecs.component.RemoveComponent
import com.github.quillraven.darkmatter.event.EventType
import com.github.quillraven.darkmatter.event.GameEventManager
import ktx.ashley.allOf
import ktx.ashley.get

class RemoveSystem(
    private val gameEventManager: GameEventManager
) : IteratingSystem(allOf(RemoveComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[RemoveComponent.mapper]?.let { remove ->
            remove.delay -= deltaTime
            if (remove.delay <= 0f) {
                entity[PlayerComponent.mapper]?.let { player ->
                    gameEventManager.dispatchEvent(EventType.PLAYER_DEATH, player.distance)
                }

                engine.removeEntity(entity)
            }
        }
    }
}
