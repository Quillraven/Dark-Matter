package com.github.quillraven.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.github.quillraven.darkmatter.ecs.component.PlayerComponent
import com.github.quillraven.darkmatter.ecs.component.RemoveComponent
import com.github.quillraven.darkmatter.ecs.component.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import kotlin.math.max

private const val DAMAGE_PER_SECOND = 25f

class DamageSystem :
    IteratingSystem(allOf(PlayerComponent::class, TransformComponent::class).exclude(RemoveComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[TransformComponent.mapper]?.let { transform ->
            entity[PlayerComponent.mapper]?.let { player ->
                if (transform.position.y <= 4f) {
                    var damage = DAMAGE_PER_SECOND * deltaTime
                    if (player.shield > 0f) {
                        val blockAmount = player.shield
                        player.shield = max(player.shield - damage, 0f)
                        damage -= blockAmount
                        if (damage <= 0f) {
                            // entire damage was blocked
                            return
                        }
                    }

                    player.life -= damage
                    if (player.life <= 0f) {
                        entity.add(engine.createComponent(RemoveComponent::class.java).apply {
                            delay = 1f
                        })
                    }
                }
            }
        }
    }
}
