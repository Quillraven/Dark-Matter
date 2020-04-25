package com.github.quillraven.darkmatter.ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.github.quillraven.darkmatter.V_WIDTH
import com.github.quillraven.darkmatter.asset.SoundAsset
import com.github.quillraven.darkmatter.audio.AudioService
import com.github.quillraven.darkmatter.ecs.component.AnimationComponent
import com.github.quillraven.darkmatter.ecs.component.GraphicComponent
import com.github.quillraven.darkmatter.ecs.component.MoveComponent
import com.github.quillraven.darkmatter.ecs.component.PlayerComponent
import com.github.quillraven.darkmatter.ecs.component.PowerUpComponent
import com.github.quillraven.darkmatter.ecs.component.PowerUpType
import com.github.quillraven.darkmatter.ecs.component.RemoveComponent
import com.github.quillraven.darkmatter.ecs.component.TransformComponent
import com.github.quillraven.darkmatter.event.GameEventManager
import com.github.quillraven.darkmatter.event.GameEventPowerUp
import com.github.quillraven.darkmatter.event.GameEventType
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.exclude
import ktx.ashley.get
import ktx.ashley.with
import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf
import ktx.log.debug
import ktx.log.error
import ktx.log.logger
import kotlin.math.min

private val LOG = logger<PowerUpSystem>()
private const val MAX_SPAWN_INTERVAL = 1.5f
private const val MIN_SPAWN_INTERVAL = 0.9f
private const val POWER_UP_SPEED = -8.75f
private const val BOOST_1_SPEED_GAIN = 3f
private const val BOOST_2_SPEED_GAIN = 3.75f
private const val LIFE_GAIN = 25f
private const val SHIELD_GAIN = 25f

private class SpawnPattern(
    type1: PowerUpType = PowerUpType.NONE,
    type2: PowerUpType = PowerUpType.NONE,
    type3: PowerUpType = PowerUpType.NONE,
    type4: PowerUpType = PowerUpType.NONE,
    type5: PowerUpType = PowerUpType.NONE,
    val types: GdxArray<PowerUpType> = gdxArrayOf(type1, type2, type3, type4, type5)
)

class PowerUpSystem(
    private val gameEventManager: GameEventManager,
    private val audioService: AudioService
) : IteratingSystem(allOf(PowerUpComponent::class, TransformComponent::class).exclude(RemoveComponent::class).get()) {
    private val playerBoundingRect = Rectangle()
    private val powerUpBoundingRect = Rectangle()
    private val playerEntities by lazy {
        engine.getEntitiesFor(
            allOf(PlayerComponent::class).exclude(RemoveComponent::class).get()
        )
    }
    private var spawnTime = 0f
    private val spawnPatterns = gdxArrayOf(
        SpawnPattern(type1 = PowerUpType.SPEED_1, type2 = PowerUpType.SPEED_2, type5 = PowerUpType.SHIELD),
        SpawnPattern(type1 = PowerUpType.SPEED_2, type2 = PowerUpType.LIFE, type5 = PowerUpType.SPEED_1),
        SpawnPattern(type2 = PowerUpType.SPEED_1, type4 = PowerUpType.SPEED_1, type5 = PowerUpType.SPEED_1),
        SpawnPattern(type2 = PowerUpType.SPEED_1, type4 = PowerUpType.SPEED_1),
        SpawnPattern(
            type1 = PowerUpType.SHIELD,
            type2 = PowerUpType.SHIELD,
            type4 = PowerUpType.LIFE,
            type5 = PowerUpType.SPEED_2
        )
    )
    private val currentSpawnPattern = GdxArray<PowerUpType>(spawnPatterns.size)

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        spawnTime -= deltaTime
        if (spawnTime <= 0f) {
            spawnTime = MathUtils.random(MIN_SPAWN_INTERVAL, MAX_SPAWN_INTERVAL)

            if (currentSpawnPattern.isEmpty) {
                currentSpawnPattern.addAll(spawnPatterns[MathUtils.random(0, spawnPatterns.size - 1)].types)
                LOG.debug { "Next pattern: $currentSpawnPattern" }
            }

            val powerUpType = currentSpawnPattern.removeIndex(0)
            if (powerUpType == PowerUpType.NONE) {
                // nothing to spawn
                return
            }

            spawnPowerUp(powerUpType, x = 1f * MathUtils.random(0, V_WIDTH - 1), y = 16f)
        }
    }

    fun spawnPowerUp(powerUpType: PowerUpType, x: Float, y: Float) {
        engine.entity {
            with<TransformComponent> {
                setInitialPosition(x, y, 0f)
                LOG.debug { "Spawn power of type $powerUpType at $position" }
            }
            with<PowerUpComponent> {
                type = powerUpType
            }
            with<AnimationComponent> {
                type = powerUpType.animationType
            }
            with<GraphicComponent>()
            with<MoveComponent> {
                speed.y = POWER_UP_SPEED
            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| must have a TransformComponent. entity=$entity" }

        if (transform.position.y <= 1f) {
            entity.addComponent<RemoveComponent>(engine)
            return
        }

        playerEntities.forEach { player ->
            player[TransformComponent.mapper]?.let { playerTransform ->
                playerBoundingRect.set(
                    playerTransform.position.x,
                    playerTransform.position.y,
                    playerTransform.size.x,
                    playerTransform.size.y
                )
                powerUpBoundingRect.set(
                    transform.position.x,
                    transform.position.y,
                    transform.size.x,
                    transform.size.y
                )

                if (playerBoundingRect.overlaps(powerUpBoundingRect)) {
                    collectPowerUp(player, entity)
                }
            }
        }
    }

    private fun collectPowerUp(player: Entity, powerUp: Entity) {
        powerUp[PowerUpComponent.mapper]?.let { powerUpCmp ->
            LOG.debug { "Picking up power of type ${powerUpCmp.type}" }

            when (powerUpCmp.type) {
                PowerUpType.SPEED_1 -> {
                    player[MoveComponent.mapper]?.let { it.speed.y += BOOST_1_SPEED_GAIN }
                    audioService.play(SoundAsset.BOOST_1)
                }
                PowerUpType.SPEED_2 -> {
                    player[MoveComponent.mapper]?.let { it.speed.y += BOOST_2_SPEED_GAIN }
                    audioService.play(SoundAsset.BOOST_2)
                }
                PowerUpType.LIFE -> player[PlayerComponent.mapper]?.let {
                    it.life = min(it.maxLife, it.life + LIFE_GAIN)
                    audioService.play(SoundAsset.LIFE)
                }
                PowerUpType.SHIELD -> player[PlayerComponent.mapper]?.let {
                    it.shield = min(it.maxShield, it.shield + SHIELD_GAIN)
                    audioService.play(SoundAsset.SHIELD)
                }
                else -> LOG.error { "Unsupported power of type ${powerUpCmp.type}" }
            }
            powerUp.addComponent<RemoveComponent>(engine)

            gameEventManager.dispatchEvent(GameEventType.POWER_UP, GameEventPowerUp.apply {
                this.player = player
                type = powerUpCmp.type
            })
        }
    }

    fun reset() {
        spawnTime = 0f
        entities.forEach {
            it.addComponent<RemoveComponent>(engine)
        }
    }
}
