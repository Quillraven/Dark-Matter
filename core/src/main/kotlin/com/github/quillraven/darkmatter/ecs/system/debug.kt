package com.github.quillraven.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.github.quillraven.darkmatter.ecs.component.PlayerComponent
import com.github.quillraven.darkmatter.ecs.component.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get

class DebugSystem : IntervalIteratingSystem(allOf(PlayerComponent::class).get(), 0.25f) {
    override fun processEntity(entity: Entity) {
        entity[PlayerComponent.mapper]?.let { player ->
            entity[TransformComponent.mapper]?.let { transform ->
                if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
                    // kill player
                    transform.position.y = 1f
                    player.life = 1f
                    player.shield = 0f
                }

                Gdx.graphics.setTitle("Dark Matter Debug - pos:${transform.position}, life:${player.life}, shield:${player.shield}")
            }
        }
    }
}
