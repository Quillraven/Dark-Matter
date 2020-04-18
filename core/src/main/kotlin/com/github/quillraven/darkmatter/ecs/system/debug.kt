package com.github.quillraven.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils
import com.github.quillraven.darkmatter.audio.AudioService
import com.github.quillraven.darkmatter.audio.SoundAsset
import com.github.quillraven.darkmatter.ecs.component.PlayerComponent
import com.github.quillraven.darkmatter.ecs.component.PowerUpType
import com.github.quillraven.darkmatter.ecs.component.TransformComponent
import com.github.quillraven.darkmatter.event.GameEventManager
import com.github.quillraven.darkmatter.event.GameEventPlayerDamaged
import com.github.quillraven.darkmatter.event.GameEventType
import ktx.ashley.allOf
import ktx.ashley.get
import kotlin.math.max
import kotlin.math.min

class DebugSystem(
    private val gameEventManager: GameEventManager,
    private val audioService: AudioService
) : IntervalIteratingSystem(allOf(PlayerComponent::class).get(), 0.25f) {
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
                        player.shield = min(player.maxShield, player.shield + 25f)
                    }
                    Gdx.input.isKeyPressed(Input.Keys.NUM_3) -> {
                        // remove shield
                        player.shield = max(0f, player.shield - 25f)
                    }
                    Gdx.input.isKeyPressed(Input.Keys.NUM_4) -> {
                        // disable movement
                        engine.getSystem(MoveSystem::class.java).setProcessing(false)
                    }
                    Gdx.input.isKeyPressed(Input.Keys.NUM_5) -> {
                        // enable movement
                        engine.getSystem(MoveSystem::class.java).setProcessing(true)
                    }
                    Gdx.input.isKeyPressed(Input.Keys.NUM_6) -> {
                        // trigger player damage event
                        player.life = max(1f, player.life - 5f)
                        gameEventManager.dispatchEvent(GameEventType.PLAYER_DAMAGED, GameEventPlayerDamaged.apply {
                            this.player = entity
                        })
                    }
                    Gdx.input.isKeyPressed(Input.Keys.NUM_7) -> {
                        // trigger player heal event
                        engine.getSystem(PowerUpSystem::class.java)
                            .spawnPowerUp(PowerUpType.LIFE, transform.position.x, transform.position.y)
                    }
                    Gdx.input.isKeyPressed(Input.Keys.NUM_8) -> {
                        // play three random sounds
                        repeat(3) {
                            audioService.play(SoundAsset.values()[MathUtils.random(0, SoundAsset.values().size - 1)])
                        }
                    }
                }

                Gdx.graphics.setTitle("Dark Matter Debug - pos:${transform.position}, life:${player.life}, shield:${player.shield}")
            }
        }
    }
}
