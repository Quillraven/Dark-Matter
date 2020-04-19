package com.github.quillraven.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.github.quillraven.darkmatter.ecs.component.GraphicComponent
import com.github.quillraven.darkmatter.ecs.component.PlayerComponent
import com.github.quillraven.darkmatter.ecs.component.PowerUpType
import com.github.quillraven.darkmatter.ecs.component.RemoveComponent
import com.github.quillraven.darkmatter.event.GameEvent
import com.github.quillraven.darkmatter.event.GameEventListener
import com.github.quillraven.darkmatter.event.GameEventManager
import com.github.quillraven.darkmatter.event.GameEventPlayerDamaged
import com.github.quillraven.darkmatter.event.GameEventPowerUp
import com.github.quillraven.darkmatter.event.GameEventType
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import kotlin.math.min

private const val TIME_TO_REACH_TARGET_COLOR = 0.25f // in seconds

class PlayerColorSystem(
    private val gameEventManager: GameEventManager
) : IteratingSystem(allOf(PlayerComponent::class, GraphicComponent::class).exclude(RemoveComponent::class).get()),
    GameEventListener {
    private var sourceColor = 0f
    private var targetColor = 0f
    private var progress = 1f

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        gameEventManager.addListener(GameEventType.PLAYER_DAMAGED, this)
        gameEventManager.addListener(GameEventType.POWER_UP, this)
    }

    override fun removedFromEngine(engine: Engine?) {
        super.removedFromEngine(engine)
        gameEventManager.removeListener(this)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val graphic = entity[GraphicComponent.mapper]
        require(graphic != null) { "Entity |entity| must have a GraphicComponent. entity=$entity" }

        val color = graphic.sprite.color
        val newGB = MathUtils.lerp(sourceColor, targetColor, progress)
        graphic.sprite.setColor(
            color.r,
            newGB,
            newGB,
            color.a
        )
    }

    override fun update(deltaTime: Float) {
        if (progress < 1f) {
            progress = min(1f, progress + deltaTime * (1f / TIME_TO_REACH_TARGET_COLOR))
            super.update(deltaTime)
        }
    }

    private fun setNewTargetColor(player: Entity) {
        progress = 0f
        player[GraphicComponent.mapper]?.let { graphic ->
            player[PlayerComponent.mapper]?.let { playerCmp ->
                val color = graphic.sprite.color
                sourceColor = color.g
                targetColor = playerCmp.life / playerCmp.maxLife
            }
        }
    }

    override fun onEvent(type: GameEventType, data: GameEvent?) {
        if (type == GameEventType.PLAYER_DAMAGED) {
            // player gets damaged -> change to new color
            setNewTargetColor((data as GameEventPlayerDamaged).player)
        } else if (type == GameEventType.POWER_UP && (data as GameEventPowerUp).type == PowerUpType.LIFE) {
            // player gets healed -> change to new color
            setNewTargetColor(data.player)
        }
    }
}
