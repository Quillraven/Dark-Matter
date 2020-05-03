package com.github.quillraven.darkmatter.ui

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.github.quillraven.darkmatter.asset.BitmapFontAsset
import com.github.quillraven.darkmatter.asset.TextureAtlasAsset
import ktx.assets.async.AssetStorage
import ktx.scene2d.Scene2DSkin
import ktx.style.imageButton
import ktx.style.label
import ktx.style.scrollPane
import ktx.style.skin
import ktx.style.textButton
import ktx.style.window

enum class SkinLabel {
    LARGE, DEFAULT
}

enum class SkinImageButton {
    PAUSE_PLAY, QUIT, SOUND_ON_OFF
}

enum class SkinTextButton {
    DEFAULT, TRANSPARENT
}

enum class SkinWindow {
    DEFAULT
}

enum class SkinScrollPane {
    DEFAULT
}

enum class SkinImage(val atlasKey: String) {
    GAME_HUD("game_hud"),
    WARNING("warning"),
    LIFE_BAR("life_bar"),
    SHIELD_BAR("shield_bar"),
    PLAY("play"),
    PAUSE("pause"),
    QUIT("quit"),
    FRAME("frame"),
    FRAME_TRANSPARENT("frame_transparent"),
    SOUND_ON("sound"),
    SOUND_OFF("no_sound"),
    SCROLL_V("scroll_v"),
    SCROLL_KNOB("scroll_knob")
}

fun createSkin(assets: AssetStorage) {
    val atlas = assets[TextureAtlasAsset.UI.descriptor]
    val bigFont = assets[BitmapFontAsset.FONT_LARGE_GRADIENT.descriptor]
    val defaultFont = assets[BitmapFontAsset.FONT_DEFAULT.descriptor]
    Scene2DSkin.defaultSkin = skin(atlas) { skin ->
        createLabelStyles(bigFont, defaultFont)
        createImageButtonStyles(skin)
        createTextButtonStyles(defaultFont, skin)
        createWindowStyles(skin, defaultFont)
        createScrollPaneStyles(skin)
    }
}

private fun Skin.createScrollPaneStyles(skin: Skin) {
    scrollPane(SkinScrollPane.DEFAULT.name) {
        vScroll = skin.getDrawable(SkinImage.SCROLL_V.atlasKey)
        vScrollKnob = skin.getDrawable(SkinImage.SCROLL_KNOB.atlasKey)
    }
}

private fun Skin.createWindowStyles(
    skin: Skin,
    defaultFont: BitmapFont
) {
    window(SkinWindow.DEFAULT.name) {
        background = skin.getDrawable(SkinImage.FRAME.atlasKey)
        titleFont = defaultFont
    }
}

private fun Skin.createTextButtonStyles(
    defaultFont: BitmapFont,
    skin: Skin
) {
    textButton(SkinTextButton.DEFAULT.name) {
        font = defaultFont
        up = skin.getDrawable(SkinImage.FRAME.atlasKey)
        down = up
    }
    textButton(SkinTextButton.TRANSPARENT.name) {
        font = defaultFont
        up = skin.getDrawable(SkinImage.FRAME_TRANSPARENT.atlasKey)
        down = up
    }
}

private fun Skin.createImageButtonStyles(skin: Skin) {
    imageButton(SkinImageButton.PAUSE_PLAY.name) {
        imageUp = skin.getDrawable(SkinImage.PAUSE.atlasKey)
        imageChecked = skin.getDrawable(SkinImage.PLAY.atlasKey)
        imageDown = imageChecked
        up = skin.getDrawable(SkinImage.FRAME.atlasKey)
        down = up
    }
    imageButton(SkinImageButton.QUIT.name) {
        imageDown = skin.getDrawable(SkinImage.QUIT.atlasKey)
        imageUp = imageDown
        up = skin.getDrawable(SkinImage.FRAME.atlasKey)
        down = up
    }
    imageButton(SkinImageButton.SOUND_ON_OFF.name) {
        imageUp = skin.getDrawable(SkinImage.SOUND_ON.atlasKey)
        imageChecked = skin.getDrawable(SkinImage.SOUND_OFF.atlasKey)
        imageDown = imageChecked
        up = skin.getDrawable(SkinImage.FRAME.atlasKey)
        down = up
    }
}

private fun Skin.createLabelStyles(
    bigFont: BitmapFont,
    defaultFont: BitmapFont
) {
    label(SkinLabel.LARGE.name) {
        font = bigFont
    }
    label(SkinLabel.DEFAULT.name) {
        font = defaultFont
    }
}
