package com.github.quillraven.darkmatter.screen

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.viewport.FitViewport
import com.github.quillraven.darkmatter.Game
import com.github.quillraven.darkmatter.UNIT_SCALE
import com.github.quillraven.darkmatter.V_HEIGHT
import com.github.quillraven.darkmatter.V_WIDTH
import com.github.quillraven.darkmatter.ecs.component.GraphicComponent
import com.github.quillraven.darkmatter.ecs.component.TransformComponent
import com.github.quillraven.darkmatter.ecs.system.PlayerInputSystem
import com.github.quillraven.darkmatter.ecs.system.RenderSystem
import ktx.app.KtxScreen
import ktx.ashley.entity
import ktx.assets.async.AssetStorage
import ktx.log.logger

private val LOG = logger<GameScreen>()

class GameScreen(
    private val game: Game,
    private val batch: Batch = game.batch,
    private val assets: AssetStorage = game.assets
) : KtxScreen {
    private val viewport = FitViewport(V_WIDTH.toFloat(), V_HEIGHT.toFloat())
    private val engine = PooledEngine().apply {
        val player = entity {
            with<TransformComponent>()
            with<GraphicComponent> {
                val region = assets.get<TextureAtlas>("graphics/graphics.atlas").findRegion("ship_base")
                sprite.run {
                    setRegion(region)
                    setSize(region.regionWidth * UNIT_SCALE, region.regionHeight * UNIT_SCALE)
                    setOriginCenter()
                }
            }
        }

        addSystem(PlayerInputSystem(player))
        addSystem(RenderSystem(batch, viewport))
    }

    override fun show() {
        LOG.debug { "Show" }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        engine.update(delta)
    }

    override fun dispose() {
        LOG.debug { "Dispose" }
        LOG.debug { "Number of entities: ${engine.entities.size()}" }
    }
}
