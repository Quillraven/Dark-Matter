package com.github.quillraven.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils
import com.github.quillraven.darkmatter.asset.SoundAsset
import com.github.quillraven.darkmatter.audio.AudioService
import com.github.quillraven.darkmatter.ecs.component.PlayerComponent
import com.github.quillraven.darkmatter.ecs.component.PowerUpType
import com.github.quillraven.darkmatter.ecs.component.TransformComponent
import com.github.quillraven.darkmatter.event.GameEventManager
import com.github.quillraven.darkmatter.event.GameEventPlayerBlock
import com.github.quillraven.darkmatter.event.GameEventPlayerHit
import com.github.quillraven.darkmatter.event.GameEventPowerUp
import com.github.quillraven.darkmatter.event.GameEventType
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.getSystem
import kotlin.math.max
import kotlin.math.min

private const val SHIELD_GAIN = 25f
private const val PLAYER_DAMAGE = 5f
private const val NUM_SOUNDS_TO_TEST = 3
private const val WINDOW_INFO_UPDATE_INTERVAL = 0.25f

class DebugSystem(
    private val gameEventManager: GameEventManager,
    private val audioService: AudioService
) : IntervalIteratingSystem(allOf(PlayerComponent::class).get(), WINDOW_INFO_UPDATE_INTERVAL) {
    init {
        setProcessing(false)
    }

    override fun processEntity(entity: Entity) {
        entity[PlayerComponent.mapper]?.let { player ->
            entity[TransformComponent.mapper]?.let { transform ->
                when {
                    Gdx.input.isKeyPressed(Input.Keys.NUM_1) -> {
                        // kill player
                        transform.position.y = 1f
                        player.life = 1f
                        player.shield = 0f
                    }
                    Gdx.input.isKeyPressed(Input.Keys.NUM_2) -> {
                        // add shield
                        player.shield = min(player.maxShield, player.shield + SHIELD_GAIN)
                        gameEventManager.dispatchEvent(GameEventType.POWER_UP, GameEventPowerUp.apply {
                            type = PowerUpType.SHIELD
                            this.player = entity
                        })
                    }
                    Gdx.input.isKeyPressed(Input.Keys.NUM_3) -> {
                        // remove shield
                        player.shield = max(0f, player.shield - SHIELD_GAIN)
                        gameEventManager.dispatchEvent(GameEventType.PLAYER_BLOCK, GameEventPlayerBlock.apply {
                            shield = player.shield
                            maxShield = player.maxShield
                        })
                    }
                    Gdx.input.isKeyPressed(Input.Keys.NUM_4) -> {
                        // disable movement
                        engine.getSystem<MoveSystem>().setProcessing(false)
                    }
                    Gdx.input.isKeyPressed(Input.Keys.NUM_5) -> {
                        // enable movement
                        engine.getSystem<MoveSystem>().setProcessing(true)
                    }
                    Gdx.input.isKeyPressed(Input.Keys.NUM_6) -> {
                        // trigger player damage event
                        player.life = max(1f, player.life - PLAYER_DAMAGE)
                        gameEventManager.dispatchEvent(GameEventType.PLAYER_HIT, GameEventPlayerHit.apply {
                            this.player = entity
                            life = player.life
                            maxLife = player.maxLife
                        })
                    }
                    Gdx.input.isKeyPressed(Input.Keys.NUM_7) -> {
                        // trigger player heal event
                        engine.getSystem<PowerUpSystem>()
                            .spawnPowerUp(PowerUpType.LIFE, transform.position.x, transform.position.y)
                    }
                    Gdx.input.isKeyPressed(Input.Keys.NUM_8) -> {
                        // play three random sounds
                        repeat(NUM_SOUNDS_TO_TEST) {
                            audioService.play(SoundAsset.values()[MathUtils.random(0, SoundAsset.values().size - 1)])
                        }
                    }
                }

                Gdx.graphics.setTitle(
                    "Dark Matter Debug - pos:${transform.position}, life:${player.life}, shield:${player.shield}"
                )
            }
        }
    }
}
