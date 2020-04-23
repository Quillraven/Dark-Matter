package com.github.quillraven.darkmatter.ecs

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.math.Vector2
import com.github.quillraven.darkmatter.UNIT_SCALE
import com.github.quillraven.darkmatter.V_HEIGHT
import com.github.quillraven.darkmatter.V_WIDTH
import com.github.quillraven.darkmatter.ecs.component.AnimationComponent
import com.github.quillraven.darkmatter.ecs.component.AnimationType.DARK_MATTER
import com.github.quillraven.darkmatter.ecs.component.AnimationType.FIRE
import com.github.quillraven.darkmatter.ecs.component.AttachComponent
import com.github.quillraven.darkmatter.ecs.component.FacingComponent
import com.github.quillraven.darkmatter.ecs.component.GraphicComponent
import com.github.quillraven.darkmatter.ecs.component.MoveComponent
import com.github.quillraven.darkmatter.ecs.component.PlayerComponent
import com.github.quillraven.darkmatter.ecs.component.TransformComponent
import com.github.quillraven.darkmatter.ecs.system.DAMAGE_AREA_HEIGHT
import ktx.ashley.entity

private const val SHIP_FIRE_OFFSET_X = 1f // in pixels
private const val SHIP_FIRE_OFFSET_Y = -6f // in pixels
const val PLAYER_START_SPEED = 3f

fun Engine.createPlayer(playerGraphicSize: Vector2) {
    // ship
    val ship = entity {
        with<PlayerComponent>()
        with<FacingComponent>()
        with<MoveComponent> {
            speed.y = PLAYER_START_SPEED
        }
        with<TransformComponent> {
            setInitialPosition(
                V_WIDTH * 0.5f - size.x * 0.5f,
                V_HEIGHT * 0.5f - size.y * 0.5f,
                1f
            )
            size.set(
                playerGraphicSize.x * UNIT_SCALE,
                playerGraphicSize.y * UNIT_SCALE
            )
        }
        with<GraphicComponent>()
    }

    // fire effect of ship
    entity {
        with<TransformComponent>()
        with<AttachComponent> {
            entity = ship
            offset.set(
                SHIP_FIRE_OFFSET_X * UNIT_SCALE,
                SHIP_FIRE_OFFSET_Y * UNIT_SCALE
            )
        }
        with<GraphicComponent>()
        with<AnimationComponent> {
            type = FIRE
        }
    }
}

fun Engine.createDarkMatter() {
    entity {
        with<TransformComponent> {
            size.set(
                V_WIDTH.toFloat(),
                DAMAGE_AREA_HEIGHT
            )
        }
        with<AnimationComponent> {
            type = DARK_MATTER
        }
        with<GraphicComponent>()
    }
}
