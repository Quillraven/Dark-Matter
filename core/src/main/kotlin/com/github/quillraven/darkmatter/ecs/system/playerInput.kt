package com.github.quillraven.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.github.quillraven.darkmatter.ecs.component.TransformComponent
import ktx.ashley.get

class PlayerInputSystem(private val player: Entity) : EntitySystem() {
    private val transform = player[TransformComponent.mapper]!!

    override fun update(deltaTime: Float) {
        when {
            Gdx.input.isKeyJustPressed(Input.Keys.LEFT) -> {
                transform.position.x -= 3 * deltaTime
            }
            Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) -> {
                transform.position.x += 3 * deltaTime
            }
            Gdx.input.isKeyJustPressed(Input.Keys.R) -> {
                transform.rotationDeg += 10
            }
        }
    }
}
