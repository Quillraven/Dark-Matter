package com.github.quillraven.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.github.quillraven.darkmatter.V_WIDTH
import com.github.quillraven.darkmatter.ecs.component.FacingComponent
import com.github.quillraven.darkmatter.ecs.component.FacingDirection
import com.github.quillraven.darkmatter.ecs.component.MoveComponent
import com.github.quillraven.darkmatter.ecs.component.RemoveComponent
import com.github.quillraven.darkmatter.ecs.component.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import kotlin.math.max
import kotlin.math.min

private const val ACCELERATION = 16.5f
private const val MAX_SPEED = 5.5f

class HorizontalMoveSystem :
    IteratingSystem(
        allOf(FacingComponent::class, MoveComponent::class, TransformComponent::class).exclude(
            RemoveComponent::class
        ).get()
    ) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[FacingComponent.mapper]?.let { facing ->
            entity[MoveComponent.mapper]?.let { move ->
                entity[TransformComponent.mapper]?.let { transform ->
                    move.speed.x = when (facing.direction) {
                        FacingDirection.LEFT -> min(0f, move.speed.x - ACCELERATION * deltaTime)
                        FacingDirection.RIGHT -> max(0f, move.speed.x + ACCELERATION * deltaTime)
                        else -> 0f
                    }

                    move.speed.x = MathUtils.clamp(move.speed.x, -MAX_SPEED, MAX_SPEED)

                    transform.position.x = MathUtils.clamp(
                        transform.position.x + move.speed.x * deltaTime,
                        0f,
                        V_WIDTH - transform.size.x
                    )
                }
            }
        }
    }
}
