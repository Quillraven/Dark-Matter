package com.github.quillraven.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.github.quillraven.darkmatter.V_HEIGHT
import com.github.quillraven.darkmatter.ecs.component.MoveComponent
import com.github.quillraven.darkmatter.ecs.component.PlayerComponent
import com.github.quillraven.darkmatter.ecs.component.RemoveComponent
import com.github.quillraven.darkmatter.ecs.component.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import kotlin.math.abs

private const val ACCELERATION = 2.25f
private const val MAX_SPEED = 0.75f

class VerticalMoveSystem :
    IteratingSystem(
        allOf(PlayerComponent::class, TransformComponent::class, MoveComponent::class).exclude(
            RemoveComponent::class
        ).get()
    ) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[MoveComponent.mapper]?.let { move ->
            entity[TransformComponent.mapper]?.let { transform ->
                entity[PlayerComponent.mapper]?.let { player ->
                    move.speed.y = MathUtils.clamp(move.speed.y + ACCELERATION * deltaTime, -MAX_SPEED, MAX_SPEED)
                    val oldY = transform.position.y
                    transform.position.y = MathUtils.clamp(
                        transform.position.y - move.speed.y * deltaTime,
                        0f,
                        V_HEIGHT - transform.size.y
                    )
                    player.distance += abs(transform.position.y - oldY)
                }
            }
        }
    }
}
