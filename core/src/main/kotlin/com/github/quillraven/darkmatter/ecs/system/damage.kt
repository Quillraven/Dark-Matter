package com.github.quillraven.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.github.quillraven.darkmatter.ecs.component.AnimationComponent
import com.github.quillraven.darkmatter.ecs.component.AnimationType
import com.github.quillraven.darkmatter.ecs.component.GraphicComponent
import com.github.quillraven.darkmatter.ecs.component.PlayerComponent
import com.github.quillraven.darkmatter.ecs.component.RemoveComponent
import com.github.quillraven.darkmatter.ecs.component.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.exclude
import ktx.ashley.get
import kotlin.math.max

private const val DAMAGE_PER_SECOND = 25f

class DamageSystem :
    IteratingSystem(allOf(PlayerComponent::class, TransformComponent::class).exclude(RemoveComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[TransformComponent.mapper]?.let { transform ->
            entity[PlayerComponent.mapper]?.let { player ->
                if (transform.position.y <= 2f) {
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
                        entity[GraphicComponent.mapper]?.sprite?.setAlpha(0f)
                        engine.entity {
                            with<TransformComponent> {
                                size.set(1.5f, 1.5f)
                                position.set(transform.position).z = 2f
                            }
                            with<AnimationComponent> {
                                type = AnimationType.EXPLOSION
                            }
                            with<GraphicComponent>()
                            with<RemoveComponent> {
                                delay = 0.9f
                            }
                        }
                    }
                }
            }
        }
    }
}
