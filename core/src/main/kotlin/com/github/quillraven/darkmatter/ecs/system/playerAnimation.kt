package com.github.quillraven.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.github.quillraven.darkmatter.UNIT_SCALE
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
        entity[FacingComponent.mapper]?.let { facing ->
            if (facing.direction == lastDirection) {
                return
            }

            lastDirection = facing.direction
            entity[GraphicComponent.mapper]?.let { graphic ->
                graphic.sprite.run {
                    val region = when (facing.direction) {
                        FacingDirection.RIGHT -> rightRegion
                        FacingDirection.LEFT -> leftRegion
                        else -> defaultRegion
                    }

                    setRegion(region)
                    setSize(region.regionWidth * UNIT_SCALE, region.regionHeight * UNIT_SCALE)
                    setOriginCenter()
                }
            }
        }
    }
}
