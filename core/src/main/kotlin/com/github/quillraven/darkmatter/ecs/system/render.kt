package com.github.quillraven.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.darkmatter.ecs.component.GraphicComponent
import com.github.quillraven.darkmatter.ecs.component.RemoveComponent
import com.github.quillraven.darkmatter.ecs.component.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import ktx.graphics.use
import ktx.log.logger
import ktx.math.component1
import ktx.math.component2

private val LOG = logger<RenderSystem>()

class RenderSystem(
    private val batch: Batch,
    private val gameViewport: Viewport,
    private val camera: Camera = gameViewport.camera
) : SortedIteratingSystem(
    allOf(GraphicComponent::class, TransformComponent::class).get(),
    compareBy { entity -> entity[TransformComponent.mapper] }
) {
    override fun update(deltaTime: Float) {
        forceSort()
        gameViewport.apply()
        batch.use(camera.combined) {
            super.update(deltaTime)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[TransformComponent.mapper]?.let { transform ->
            val (posX, posY) = transform.position
            val (sizeX, sizeY) = transform.size
            entity[GraphicComponent.mapper]?.let { graphic ->
                if (graphic.sprite.texture == null) {
                    LOG.error { "Entity has no texture for rendering" }
                    return
                }

                graphic.sprite.run {
                    rotation = transform.rotationDeg
                    setBounds(posX, posY, sizeX, sizeY)
                    draw(batch)
                }
            }
        }
    }
}
