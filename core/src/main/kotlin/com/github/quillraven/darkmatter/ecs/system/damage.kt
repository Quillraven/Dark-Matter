package com.github.quillraven.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.github.quillraven.darkmatter.asset.SoundAsset
import com.github.quillraven.darkmatter.audio.AudioService
import com.github.quillraven.darkmatter.ecs.component.AnimationComponent
import com.github.quillraven.darkmatter.ecs.component.AnimationType
import com.github.quillraven.darkmatter.ecs.component.GraphicComponent
import com.github.quillraven.darkmatter.ecs.component.PlayerComponent
import com.github.quillraven.darkmatter.ecs.component.RemoveComponent
import com.github.quillraven.darkmatter.ecs.component.TransformComponent
import com.github.quillraven.darkmatter.event.GameEventManager
import com.github.quillraven.darkmatter.event.GameEventPlayerDamaged
import com.github.quillraven.darkmatter.event.GameEventType
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.exclude
import ktx.ashley.get
import kotlin.math.max

private const val DAMAGE_PER_SECOND = 25f
private const val DAM_SOUND_LENGTH = 0.6f
private const val BLOCK_SOUND_LENGTH = 0.6f

class DamageSystem(
    private val gameEventManager: GameEventManager,
    private val audioService: AudioService
) :
    IteratingSystem(allOf(PlayerComponent::class, TransformComponent::class).exclude(RemoveComponent::class).get()) {
    private var damageTaken = false
    private var blocked = false
    private var damageSoundCountdown = 0f
    private var blockSoundCountdown = 0f

    override fun update(deltaTime: Float) {
        damageTaken = false
        blocked = false
        super.update(deltaTime)
        if (blocked) {
            if (blockSoundCountdown <= 0f) {
                audioService.play(SoundAsset.BLOCK)
                blockSoundCountdown = BLOCK_SOUND_LENGTH
            } else {
                blockSoundCountdown -= deltaTime
            }
        }
        if (damageTaken) {
            if (damageSoundCountdown <= 0f) {
                audioService.play(SoundAsset.DAMAGE)
                damageSoundCountdown = DAM_SOUND_LENGTH
            } else {
                damageSoundCountdown -= deltaTime
            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[TransformComponent.mapper]?.let { transform ->
            entity[PlayerComponent.mapper]?.let { player ->
                if (transform.position.y <= 2f) {
                    var damage = DAMAGE_PER_SECOND * deltaTime
                    if (player.shield > 0f) {
                        val blockAmount = player.shield
                        blocked = true
                        player.shield = max(player.shield - damage, 0f)
                        damage -= blockAmount
                        if (damage <= 0f) {
                            // entire damage was blocked
                            return
                        }
                    }

                    damageTaken = true
                    player.life -= damage

                    gameEventManager.dispatchEvent(GameEventType.PLAYER_DAMAGED, GameEventPlayerDamaged.apply {
                        this.player = entity
                    })
                    if (player.life <= 0f) {
                        entity.add(engine.createComponent(RemoveComponent::class.java).apply {
                            delay = 1f
                        })
                        entity[GraphicComponent.mapper]?.sprite?.setAlpha(0f)
                        engine.entity {
                            with<TransformComponent> {
                                size.set(1.5f, 1.5f)
                                setInitialPosition(transform.position.x, transform.position.y, 2f)
                            }
                            with<AnimationComponent> {
                                type = AnimationType.EXPLOSION
                            }
                            with<GraphicComponent>()
                            with<RemoveComponent> {
                                delay = 0.9f
                            }
                        }
                        audioService.play(SoundAsset.EXPLOSION)
                    }
                }
            }
        }
    }
}
