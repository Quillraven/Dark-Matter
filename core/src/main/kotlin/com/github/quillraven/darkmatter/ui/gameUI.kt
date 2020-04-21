package com.github.quillraven.darkmatter.ui

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn
import com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut
import com.badlogic.gdx.scenes.scene2d.actions.Actions.forever
import com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence
import com.badlogic.gdx.utils.Align
import com.github.quillraven.darkmatter.V_HEIGHT_PIXELS
import com.github.quillraven.darkmatter.V_WIDTH_PIXELS
import ktx.actors.plus
import ktx.actors.plusAssign
import ktx.scene2d.image
import ktx.scene2d.imageButton
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
private const val ACTOR_FADE_IN_TIME = 0.5f
private const val ACTOR_FADE_OUT_TIME = 1f
private const val MAX_WARNING_FLASHES = 3
private const val TOUCH_TO_BEGIN_LABEL_OFFSET_Y = 80f
private const val PADDING_TOP = 2f
private const val PADDING_LEFT_RIGHT = 2f

class GameUI : Group() {
    private val warningImage = scene2d.image(SkinImage.WARNING.atlasKey) {
        color.a = 0f
    }
    private val lifeBarImage = scene2d.image(SkinImage.LIFE_BAR.atlasKey) {
        width = GAME_HUD_SMALL_AREA_WIDTH
        height = GAME_HUD_AREA_HEIGHT
    }
    private val shieldBarImage = scene2d.image(SkinImage.SHIELD_BAR.atlasKey) {
        width = GAME_HUD_SMALL_AREA_WIDTH
        height = GAME_HUD_AREA_HEIGHT
    }
    private val distanceLabel = scene2d.label("0", SkinLabel.DEFAULT.name) {
        width = GAME_HUD_LARGE_AREA_WIDTH
        setAlignment(Align.center)
    }
    private val speedLabel = scene2d.label("0", SkinLabel.DEFAULT.name) {
        width = GAME_HUD_SMALL_AREA_WIDTH
        setAlignment(Align.center)
    }
    val touchToBeginLabel = scene2d.label("Touch to begin", SkinLabel.LARGE.name) {
        y = V_HEIGHT_PIXELS - TOUCH_TO_BEGIN_LABEL_OFFSET_Y
        wrap = true
        width = V_WIDTH_PIXELS.toFloat()
        setAlignment(Align.center)
        color.a = 0f
        this += forever(sequence(fadeIn(ACTOR_FADE_IN_TIME) + fadeOut(ACTOR_FADE_OUT_TIME)))
    }
    val quitImageButton = scene2d.imageButton(SkinImageButton.QUIT.name) {
        y = V_HEIGHT_PIXELS - height - PADDING_TOP
        x = PADDING_LEFT_RIGHT
        color.a = 0.5f
    }
    val pauseResumeButton = scene2d.imageButton(SkinImageButton.PAUSE_PLAY.name) {
        y = V_HEIGHT_PIXELS - height - PADDING_TOP
        x = V_WIDTH_PIXELS - width - PADDING_LEFT_RIGHT
        color.a = 0.5f
    }

    init {
        // touch to begin label
        this += touchToBeginLabel

        // top quit, pause and play buttons
        this += quitImageButton
        this += pauseResumeButton

        // bottom game hud
        var gameHudX = 0f
        var gameHudHeight = 0f
        var gameHudWidth = 0f
        this += scene2d.image(SkinImage.GAME_HUD.atlasKey) {
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
            warningImage += sequence(fadeIn(ACTOR_FADE_IN_TIME) + fadeOut(ACTOR_FADE_OUT_TIME))
        }
    }
}
