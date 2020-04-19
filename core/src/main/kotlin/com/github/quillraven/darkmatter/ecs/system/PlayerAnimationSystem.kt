package com.github.quillraven.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.quillraven.darkmatter.ecs.component.FacingComponent
import com.github.quillraven.darkmatter.ecs.component.FacingDirection
import com.github.quillraven.darkmatter.ecs.component.GraphicComponent
import com.github.quillraven.darkmatter.ecs.component.PlayerComponent
import ktx.ashley.allOf
import ktx.ashley.get

class PlayerAnimationSystem(
    private val defaultRegion: TextureRegion,
    private val leftRegion: TextureRegion,
    private val rightRegion: TextureRegion
) : IteratingSystem(allOf(PlayerComponent::class, FacingComponent::class, GraphicComponent::class).get()) {
    private var lastDirection = FacingDirection.DEFAULT

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val facing = entity[FacingComponent.mapper]
        require(facing != null) { "Entity |entity| must have a FacingComponent. entity=$entity" }
        val graphic = entity[GraphicComponent.mapper]
        require(graphic != null) { "Entity |entity| must have a GraphicComponent. entity=$entity" }

        if (facing.direction == lastDirection && graphic.sprite.texture != null) {
            // texture already set and direction does not change
            return
        }

        lastDirection = facing.direction
        val region = when (facing.direction) {
            FacingDirection.RIGHT -> rightRegion
            FacingDirection.LEFT -> leftRegion
            else -> defaultRegion
        }
        graphic.setSpriteRegion(region)
    }
}
