package com.github.quillraven.darkmatter.ui

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align
import com.github.quillraven.darkmatter.V_WIDTH_PIXELS
import ktx.actors.plus
import ktx.actors.plusAssign
import ktx.scene2d.image
import ktx.scene2d.label
import ktx.scene2d.scene2d
import kotlin.math.roundToInt

private const val GAME_HUD_BORDER_SIZE_X = 7f
private const val GAME_HUD_BORDER_SIZE_Y = 6f
private const val GAME_HUD_SMALL_AREA_WIDTH = 23f
private const val GAME_HUD_LARGE_AREA_WIDTH = 48f
private const val GAME_HUD_AREA_HEIGHT = 9f
private const val MIN_SPEED = -99f
private const val MAX_SPEED = 999f
private const val MAX_DISTANCE = 999999f
private const val WARNING_FADE_IN_TIME = 0.5f
private const val WARNING_FADE_OUT_TIME = 1f
private const val MAX_WARNING_FLASHES = 3

class GameUI : Group() {
    private val warningImage = scene2d.image(UIImage.WARNING.atlasKey) {
        color.a = 0f
    }
    private val lifeBarImage = scene2d.image(UIImage.LIFE_BAR.atlasKey) {
        width = GAME_HUD_SMALL_AREA_WIDTH
        height = GAME_HUD_AREA_HEIGHT
    }
    private val shieldBarImage = scene2d.image(UIImage.SHIELD_BAR.atlasKey) {
        width = GAME_HUD_SMALL_AREA_WIDTH
        height = GAME_HUD_AREA_HEIGHT
    }
    private val distanceLabel = scene2d.label("0", LabelStyle.DEFAULT.name) {
        width = GAME_HUD_LARGE_AREA_WIDTH
        setAlignment(Align.center)
    }
    private val speedLabel = scene2d.label("0", LabelStyle.DEFAULT.name) {
        width = GAME_HUD_SMALL_AREA_WIDTH
        setAlignment(Align.center)
    }

    init {
        var gameHudX = 0f
        var gameHudHeight = 0f
        var gameHudWidth = 0f
        this += scene2d.image(UIImage.GAME_HUD.atlasKey) {
            gameHudX = V_WIDTH_PIXELS * 0.5f - width * 0.5f
            gameHudHeight = height
            gameHudWidth = width
            x = gameHudX
        }
        this += warningImage.apply {
            setPosition(gameHudX, gameHudHeight)
        }
        this += lifeBarImage.apply {
            setPosition(
                gameHudX + GAME_HUD_BORDER_SIZE_X,
                gameHudHeight * 0.5f - height * 0.5f
            )
        }
        this += shieldBarImage.apply {
            setPosition(
                gameHudX + GAME_HUD_BORDER_SIZE_X,
                gameHudHeight * 0.5f - height * 0.5f
            )
        }
        this += distanceLabel.apply {
            setPosition(
                gameHudX + gameHudWidth * 0.5f - GAME_HUD_LARGE_AREA_WIDTH * 0.5f,
                GAME_HUD_BORDER_SIZE_Y
            )
        }
        this += speedLabel.apply {
            setPosition(
                gameHudX + gameHudWidth - GAME_HUD_BORDER_SIZE_X - GAME_HUD_SMALL_AREA_WIDTH,
                GAME_HUD_BORDER_SIZE_Y
            )
        }
    }

    fun updateDistance(distance: Float) {
        distanceLabel.run {
            text.setLength(0)
            text.append(MathUtils.clamp(distance, 0f, MAX_DISTANCE).roundToInt())
            invalidateHierarchy()
        }
    }

    fun updateSpeed(speed: Float) {
        speedLabel.run {
            text.setLength(0)
            text.append(MathUtils.clamp(speed, MIN_SPEED, MAX_SPEED).roundToInt())
            invalidateHierarchy()
        }
    }

    fun updateLife(life: Float, maxLife: Float) {
        lifeBarImage.scaleX = MathUtils.clamp(life / maxLife, 0f, 1f)
    }

    fun updateShield(shield: Float, maxShield: Float) {
        shieldBarImage.color.a = MathUtils.clamp(shield / maxShield, 0f, 1f)
    }

    fun showWarning() {
        if (warningImage.actions.size <= MAX_WARNING_FLASHES) {
            warningImage += Actions.sequence(
                Actions.fadeIn(WARNING_FADE_IN_TIME) + Actions.fadeOut(WARNING_FADE_OUT_TIME)
            )
        }
    }
}
