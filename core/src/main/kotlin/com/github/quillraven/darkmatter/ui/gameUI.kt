package com.github.quillraven.darkmatter.ui

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Align
import com.github.quillraven.darkmatter.V_WIDTH_PIXELS
import ktx.actors.plusAssign
import ktx.scene2d.image
import ktx.scene2d.label
import ktx.scene2d.scene2d

private const val GAME_HUD_BORDER_SIZE_X = 7f
private const val GAME_HUD_BORDER_SIZE_Y = 6f
private const val GAME_HUD_SMALL_AREA_WIDTH = 23f
private const val GAME_HUD_LARGE_AREA_WIDTH = 48f
private const val GAME_HUD_AREA_HEIGHT = 9f

class GameUI : Group() {
    private val warningImage = scene2d.image(UIImage.WARNING.atlasKey)
    private val lifeBarImage = scene2d.image(UIImage.LIFE_BAR.atlasKey) {
        width = GAME_HUD_SMALL_AREA_WIDTH
        height = GAME_HUD_AREA_HEIGHT
    }
    private val shieldBarImage = scene2d.image(UIImage.SHIELD_BAR.atlasKey) {
        width = GAME_HUD_SMALL_AREA_WIDTH
        height = GAME_HUD_AREA_HEIGHT
    }
    private val distanceLabel = scene2d.label("1000", LabelStyle.DEFAULT.name) {
        width = GAME_HUD_LARGE_AREA_WIDTH
        setAlignment(Align.center)
    }
    private val speedLabel = scene2d.label("375", LabelStyle.DEFAULT.name) {
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
}
