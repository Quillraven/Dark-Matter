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
private const val MAX_NEG_PLAYER_SPEED = 0.75f
private const val MAX_POS_PLAYER_SPEED = 5f

class VerticalMoveSystem :
    IteratingSystem(
        allOf(TransformComponent::class, MoveComponent::class).exclude(RemoveComponent::class).get()
    ) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[MoveComponent.mapper]?.let { move ->
            entity[TransformComponent.mapper]?.let { transform ->
                val player = entity[PlayerComponent.mapper]
                if (player != null) {
                    move.speed.y = MathUtils.clamp(
                        move.speed.y - ACCELERATION * deltaTime,
                        -MAX_NEG_PLAYER_SPEED,
                        MAX_POS_PLAYER_SPEED
                    )
                    val oldY = transform.position.y
                    moveEntityDown(transform, move, deltaTime)
                    player.distance += abs(transform.position.y - oldY)
                } else {
                    moveEntityDown(transform, move, deltaTime)
                }
            }
        }
    }

    private fun moveEntityDown(
        transform: TransformComponent,
        move: MoveComponent,
        deltaTime: Float
    ) {
        transform.position.y = MathUtils.clamp(
            transform.position.y + move.speed.y * deltaTime,
            0f,
            V_HEIGHT + 1f - transform.size.y
        )
    }
}
