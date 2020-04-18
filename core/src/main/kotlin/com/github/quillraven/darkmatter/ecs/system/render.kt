package com.github.quillraven.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Camera
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
import com.github.quillraven.darkmatter.event.GameEventPowerUp
import com.github.quillraven.darkmatter.event.GameEventType
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import ktx.graphics.use
import ktx.log.logger
import ktx.math.component1
import ktx.math.component2
import ktx.math.vec2
import ktx.math.vec3
import kotlin.math.min

private val LOG = logger<RenderSystem>()
private const val MIN_BGD_SCROLL_SPEED_Y = -0.25f

class RenderSystem(
    private val stage: Stage,
    private val batch: Batch,
    private val outlineShader: ShaderProgram,
    private val gameViewport: Viewport,
    private val gameEventManager: GameEventManager,
    backgroundTexture: Texture,
    private val camera: Camera = gameViewport.camera
) : SortedIteratingSystem(
    allOf(GraphicComponent::class, TransformComponent::class).get(),
    compareBy { entity -> entity[TransformComponent.mapper] }
), GameEventListener {
    private val background = Sprite(backgroundTexture.apply {
        setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
    })
    private val backgroundScrollSpeed = vec2(0.03f, MIN_BGD_SCROLL_SPEED_Y)
    private val textureSizeLoc = outlineShader.getUniformLocation("u_textureSize")
    private val outlineColorLoc = outlineShader.getUniformLocation("u_outlineColor")
    private val outlineColor = vec3(0f, 113f / 255f, 214f / 255f)
    private val playerEntities by lazy {
        engine.getEntitiesFor(
            allOf(PlayerComponent::class).exclude(RemoveComponent::class).get()
        )
    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        gameEventManager.addListener(GameEventType.POWER_UP, this)
    }

    override fun removedFromEngine(engine: Engine?) {
        super.removedFromEngine(engine)
        gameEventManager.removeListener(GameEventType.POWER_UP, this)
    }

    override fun update(deltaTime: Float) {
        // render scrolling background
        stage.viewport.apply()
        batch.use(stage.camera.combined) {
            background.run {
                backgroundScrollSpeed.y = min(MIN_BGD_SCROLL_SPEED_Y, backgroundScrollSpeed.y + deltaTime * 0.1f)
                scroll(deltaTime * backgroundScrollSpeed.x, deltaTime * backgroundScrollSpeed.y)
                draw(batch)
            }
        }

        // render entities
        forceSort()
        gameViewport.apply()
        batch.use(camera.combined) {
            super.update(deltaTime)
        }

        // render player with outline shader in case he has a shield
        batch.use(camera.combined) {
            it.shader = outlineShader
            outlineShader.setUniformf(outlineColorLoc, outlineColor)
            playerEntities.forEach { entity ->
                entity[PlayerComponent.mapper]?.let { player ->
                    if (player.shield > 0f) {
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
            }
            it.shader = null
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[TransformComponent.mapper]?.let { transform ->
            val (posX, posY) = transform.interpolatedPosition
            val (sizeX, sizeY) = transform.size
            entity[GraphicComponent.mapper]?.let { graphic ->
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
        }
    }

    override fun onEvent(type: GameEventType, data: GameEvent?) {
        val eventPowerUp = data as GameEventPowerUp
        if (eventPowerUp.type == PowerUpType.SPEED_1) backgroundScrollSpeed.y -= 0.25f
        else if (eventPowerUp.type == PowerUpType.SPEED_2) backgroundScrollSpeed.y -= 0.5f
    }
}
