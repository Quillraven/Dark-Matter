package com.github.quillraven.darkmatter.screen

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.github.quillraven.darkmatter.Game
import com.github.quillraven.darkmatter.V_HEIGHT
import com.github.quillraven.darkmatter.V_WIDTH
import com.github.quillraven.darkmatter.audio.AudioService
import com.github.quillraven.darkmatter.ecs.component.AnimationComponent
import com.github.quillraven.darkmatter.ecs.component.AnimationType
import com.github.quillraven.darkmatter.ecs.component.AttachComponent
import com.github.quillraven.darkmatter.ecs.component.FacingComponent
import com.github.quillraven.darkmatter.ecs.component.GraphicComponent
import com.github.quillraven.darkmatter.ecs.component.MoveComponent
import com.github.quillraven.darkmatter.ecs.component.PlayerComponent
import com.github.quillraven.darkmatter.ecs.component.TransformComponent
import com.github.quillraven.darkmatter.ecs.system.AnimationSystem
import com.github.quillraven.darkmatter.ecs.system.AttachSystem
import com.github.quillraven.darkmatter.ecs.system.CameraShakeSystem
import com.github.quillraven.darkmatter.ecs.system.DamageSystem
import com.github.quillraven.darkmatter.ecs.system.DebugSystem
import com.github.quillraven.darkmatter.ecs.system.MoveSystem
import com.github.quillraven.darkmatter.ecs.system.PlayerAnimationSystem
import com.github.quillraven.darkmatter.ecs.system.PlayerColorSystem
import com.github.quillraven.darkmatter.ecs.system.PlayerInputSystem
import com.github.quillraven.darkmatter.ecs.system.PowerUpSystem
import com.github.quillraven.darkmatter.ecs.system.RemoveSystem
import com.github.quillraven.darkmatter.ecs.system.RenderSystem
import com.github.quillraven.darkmatter.event.GameEvent
import com.github.quillraven.darkmatter.event.GameEventListener
import com.github.quillraven.darkmatter.event.GameEventManager
import com.github.quillraven.darkmatter.event.GameEventType
import ktx.app.KtxScreen
import ktx.ashley.entity
import ktx.assets.async.AssetStorage
import ktx.log.logger
import kotlin.math.min

private val LOG = logger<GameScreen>()
private const val MAX_DELTA_TIME = 1 / 30f

class GameScreen(
    private val game: Game,
    private val batch: Batch = game.batch,
    private val assets: AssetStorage = game.assets,
    private val gameEventManager: GameEventManager = game.gameEventManager,
    private val stage: Stage = game.stage,
    private val outlineShader: ShaderProgram = game.assets["outlineShader"],
    private val audioService: AudioService = game.audioService
) : KtxScreen, GameEventListener {
    private val viewport = FitViewport(V_WIDTH.toFloat(), V_HEIGHT.toFloat())
    private val engine = PooledEngine().apply {
        val atlas = assets.get<TextureAtlas>("graphics/graphics.atlas")

        addSystem(DebugSystem(gameEventManager, audioService))
        addSystem(PowerUpSystem(gameEventManager))
        addSystem(PlayerInputSystem(viewport))
        addSystem(MoveSystem())
        addSystem(DamageSystem(gameEventManager))
        addSystem(
            PlayerAnimationSystem(
                atlas.findRegion("ship_base"),
                atlas.findRegion("ship_left"),
                atlas.findRegion("ship_right")
            )
        )
        addSystem(AttachSystem())
        addSystem(AnimationSystem(atlas))
        addSystem(CameraShakeSystem(viewport.camera, gameEventManager))
        addSystem(PlayerColorSystem(gameEventManager))
        addSystem(
            RenderSystem(
                stage,
                batch,
                outlineShader,
                viewport,
                gameEventManager,
                assets["graphics/background.png"]
            )
        )
        addSystem(RemoveSystem(gameEventManager))
    }
    private var respawn = true

    override fun show() {
        LOG.debug { "Show" }
        gameEventManager.addListener(GameEventType.PLAYER_SPAWN, this)
        gameEventManager.addListener(GameEventType.PLAYER_DEATH, this)
        spawnDarkMatter()
    }

    override fun hide() {
        LOG.debug { "Hide" }
        gameEventManager.removeListener(this)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        stage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        if (respawn) {
            spawnPlayer()
        }
        val deltaTime = min(delta, MAX_DELTA_TIME)
        engine.update(deltaTime)
    }

    override fun dispose() {
        LOG.debug { "Dispose" }
        LOG.debug { "Number of entities: ${engine.entities.size()}" }
    }

    private fun spawnPlayer() {
        respawn = false

        // ship
        val ship = engine.entity {
            with<PlayerComponent>()
            with<FacingComponent>()
            with<MoveComponent>()
            with<TransformComponent> {
                setInitialPosition(V_WIDTH * 0.5f - size.x * 0.5f, V_HEIGHT * 0.5f - size.y * 0.5f, 1f)
                size.set(10f / 8f, 9f / 8f)
            }
            with<GraphicComponent>()
        }

        // fire effect of ship
        engine.entity {
            with<TransformComponent>()
            with<AttachComponent> {
                entity = ship
                offset.set(1f / 8f, -0.7f)
            }
            with<GraphicComponent>()
            with<AnimationComponent> {
                type = AnimationType.FIRE
            }
        }

        gameEventManager.dispatchEvent(GameEventType.PLAYER_SPAWN)
    }

    private fun spawnDarkMatter() {
        engine.entity {
            with<TransformComponent> {
                size.set(9f, 2f)
            }
            with<AnimationComponent> {
                type = AnimationType.DARK_MATTER
            }
            with<GraphicComponent>()
        }
    }

    override fun onEvent(type: GameEventType, data: GameEvent?) {
        when (type) {
            GameEventType.PLAYER_SPAWN -> LOG.debug { "Spawn new player" }
            GameEventType.PLAYER_DEATH -> {
                LOG.debug { "Player died with a distance of $data" }
                respawn = true
            }
            else -> {
                // ignore
            }
        }
    }
}
