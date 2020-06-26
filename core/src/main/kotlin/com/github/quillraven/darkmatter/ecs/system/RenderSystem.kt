package com.github.quillraven.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.darkmatter.ecs.component.GraphicComponent
import com.github.quillraven.darkmatter.ecs.component.PlayerComponent
import com.github.quillraven.darkmatter.ecs.component.PowerUpType
import com.github.quillraven.darkmatter.ecs.component.RemoveComponent
import com.github.quillraven.darkmatter.ecs.component.TransformComponent
import com.github.quillraven.darkmatter.event.GameEvent
import com.github.quillraven.darkmatter.event.GameEventListener
import com.github.quillraven.darkmatter.event.GameEventManager
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import ktx.graphics.use
import ktx.log.error
import ktx.log.logger
import ktx.math.component1
import ktx.math.component2
import ktx.math.vec2
import kotlin.math.max
import kotlin.math.min

private val LOG = logger<RenderSystem>()
private const val BGD_SCROLL_SPEED_X = 0.03f
private const val MIN_BGD_SCROLL_SPEED_Y = -0.25f
private const val OUTLINE_COLOR_RED = 0f
private const val OUTLINE_COLOR_GREEN = 113f / 255f
private const val OUTLINE_COLOR_BLUE = 214f / 255f
private const val OUTLINE_COLOR_MIN_ALPHA = 0.35f
private const val BGD_SCROLL_SPEED_GAIN_BOOST_1 = 0.25f
private const val BGD_SCROLL_SPEED_GAIN_BOOST_2 = 0.5f
private const val TIME_TO_RESET_BGD_SCROLL_SPEED = 10f // in seconds

class RenderSystem(
    private val stage: Stage,
    private val outlineShader: ShaderProgram,
    private val gameViewport: Viewport,
    private val gameEventManager: GameEventManager,
    backgroundTexture: Texture
) : SortedIteratingSystem(
    allOf(GraphicComponent::class, TransformComponent::class).get(),
    compareBy { entity -> entity[TransformComponent.mapper] }
), GameEventListener {
    private val batch: Batch = stage.batch
    private val camera: Camera = gameViewport.camera
    private val background = Sprite(backgroundTexture.apply {
        setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
    })
    private val backgroundScrollSpeed = vec2(BGD_SCROLL_SPEED_X, MIN_BGD_SCROLL_SPEED_Y)
    private val textureSizeLoc = outlineShader.getUniformLocation("u_textureSize")
    private val outlineColorLoc = outlineShader.getUniformLocation("u_outlineColor")
    private val outlineColor = Color(OUTLINE_COLOR_RED, OUTLINE_COLOR_GREEN, OUTLINE_COLOR_BLUE, 1f)
    private val playerEntities by lazy {
        engine.getEntitiesFor(
            allOf(PlayerComponent::class).exclude(RemoveComponent::class).get()
        )
    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        gameEventManager.addListener(GameEvent.PowerUp::class, this)
    }

    override fun removedFromEngine(engine: Engine?) {
        super.removedFromEngine(engine)
        gameEventManager.removeListener(GameEvent.PowerUp::class, this)
    }

    override fun update(deltaTime: Float) {
        // render scrolling background
        stage.viewport.apply()
        renderBackground(deltaTime)

        // render entities
        forceSort()
        gameViewport.apply()
        batch.use(camera.combined) {
            super.update(deltaTime)
        }

        // render player with outline shader in case he has a shield
        renderEntityOutlines()
    }

    private fun renderEntityOutlines() {
        batch.use(camera.combined) {
            it.shader = outlineShader
            playerEntities.forEach { entity ->
                renderPlayerOutlines(entity, it)
            }
            it.shader = null
        }
    }

    private fun renderPlayerOutlines(entity: Entity, it: Batch) {
        val player = entity[PlayerComponent.mapper]
        require(player != null) { "Entity |entity| must have a PlayerComponent. entity=$entity" }

        if (player.shield > 0f) {
            outlineColor.a = max(OUTLINE_COLOR_MIN_ALPHA, player.shield / player.maxShield)
            outlineShader.setUniformf(outlineColorLoc, outlineColor)
            entity[GraphicComponent.mapper]?.let { graphic ->
                graphic.sprite.run {
                    outlineShader.setUniformf(
                        textureSizeLoc, vec2(
                            texture.width.toFloat(),
                            texture.height.toFloat()
                        )
                    )
                    draw(it)
                }
            }
        }
    }

    private fun renderBackground(deltaTime: Float) {
        batch.use(stage.camera.combined) {
            background.run {
                backgroundScrollSpeed.y = min(
                    MIN_BGD_SCROLL_SPEED_Y,
                    backgroundScrollSpeed.y + deltaTime * (1f / TIME_TO_RESET_BGD_SCROLL_SPEED)
                )
                scroll(deltaTime * backgroundScrollSpeed.x, deltaTime * backgroundScrollSpeed.y)
                draw(batch)
            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| must have a TransformComponent. entity=$entity" }
        val graphic = entity[GraphicComponent.mapper]
        require(graphic != null) { "Entity |entity| must have a GraphicComponent. entity=$entity" }

        val (posX, posY) = transform.interpolatedPosition
        val (sizeX, sizeY) = transform.size
        if (graphic.sprite.texture == null) {
            LOG.error { "Entity has no texture for rendering" }
            return
        }

        graphic.sprite.run {
            rotation = transform.rotationDeg
            setBounds(posX, posY, sizeX, sizeY)
            draw(batch)
        }
    }

    override fun onEvent(event: GameEvent) {
        val eventPowerUp = event as GameEvent.PowerUp
        if (eventPowerUp.type == PowerUpType.SPEED_1) {
            backgroundScrollSpeed.y -= BGD_SCROLL_SPEED_GAIN_BOOST_1
        } else if (eventPowerUp.type == PowerUpType.SPEED_2) {
            backgroundScrollSpeed.y -= BGD_SCROLL_SPEED_GAIN_BOOST_2
        }
    }
}
