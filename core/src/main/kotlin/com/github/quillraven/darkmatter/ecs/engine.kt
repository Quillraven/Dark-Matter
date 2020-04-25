package com.github.quillraven.darkmatter.ecs

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.github.quillraven.darkmatter.UNIT_SCALE
import com.github.quillraven.darkmatter.V_HEIGHT
import com.github.quillraven.darkmatter.V_WIDTH
import com.github.quillraven.darkmatter.asset.TextureAtlasAsset
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
import ktx.ashley.with
import ktx.assets.async.AssetStorage

private const val SHIP_FIRE_OFFSET_X = 1f // in pixels
private const val SHIP_FIRE_OFFSET_Y = -6f // in pixels
const val PLAYER_START_SPEED = 3f

fun Engine.createPlayer(
    assets: AssetStorage,
    spawnX: Float = V_WIDTH * 0.5f,
    spawnY: Float = V_HEIGHT * 0.5f
): Entity {
    // ship
    val ship = entity {
        with<PlayerComponent>()
        with<FacingComponent>()
        with<MoveComponent> {
            speed.y = PLAYER_START_SPEED
        }
        with<TransformComponent> {
            val atlas = assets[TextureAtlasAsset.GRAPHICS.descriptor]
            val playerGraphicRegion = atlas.findRegion("ship_base")
            size.set(
                playerGraphicRegion.originalWidth * UNIT_SCALE,
                playerGraphicRegion.originalHeight * UNIT_SCALE
            )
            setInitialPosition(
                spawnX - size.x * 0.5f,
                spawnY - size.y * 0.5f,
                1f
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

    return ship
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
