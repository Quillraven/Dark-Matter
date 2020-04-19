package com.github.quillraven.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.darkmatter.ecs.component.FacingComponent
import com.github.quillraven.darkmatter.ecs.component.FacingDirection
import com.github.quillraven.darkmatter.ecs.component.PlayerComponent
import com.github.quillraven.darkmatter.ecs.component.RemoveComponent
import com.github.quillraven.darkmatter.ecs.component.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import ktx.math.vec2

private const val TOUCH_TOLERANCE_DISTANCE = 0.1f

class PlayerInputSystem(
    private val gameViewport: Viewport
) : IteratingSystem(
    allOf(PlayerComponent::class, TransformComponent::class, FacingComponent::class).exclude(
        RemoveComponent::class
    ).get()
) {
    private val tmpVec = vec2()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val facing = entity[FacingComponent.mapper]
        require(facing != null) { "Entity |entity| must have a FacingComponent. entity=$entity" }
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| must have a TransformComponent. entity=$entity" }

        tmpVec.x = Gdx.input.x.toFloat()
        gameViewport.unproject(tmpVec)
        val distX = tmpVec.x - transform.position.x - transform.size.x * 0.5f
        facing.direction = when {
            distX < -TOUCH_TOLERANCE_DISTANCE -> FacingDirection.LEFT
            distX > TOUCH_TOLERANCE_DISTANCE -> FacingDirection.RIGHT
            else -> FacingDirection.DEFAULT
        }
    }
}
