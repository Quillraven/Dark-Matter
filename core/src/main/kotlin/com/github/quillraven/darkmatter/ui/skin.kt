package com.github.quillraven.darkmatter.ui

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.github.quillraven.darkmatter.asset.BitmapFontAsset
import com.github.quillraven.darkmatter.asset.TextureAtlasAsset
import ktx.assets.async.AssetStorage
import ktx.scene2d.Scene2DSkin
import ktx.style.SkinDsl
import ktx.style.imageButton
import ktx.style.label
import ktx.style.skin

enum class SkinLabel {
    LARGE, DEFAULT
}

enum class SkinImageButton {
    PAUSE_PLAY, QUIT
}

enum class SkinImage(val atlasKey: String) {
    GAME_HUD("game_hud"),
    WARNING("warning"),
    LIFE_BAR("life_bar"),
    SHIELD_BAR("shield_bar"),
    PLAY("play"),
    PAUSE("pause"),
    QUIT("quit")
}

fun createSkin(assets: AssetStorage) {
    val atlas = assets[TextureAtlasAsset.UI.descriptor]
    val bigFont = assets[BitmapFontAsset.FONT_LARGE_GRADIENT.descriptor]
    val defaultFont = assets[BitmapFontAsset.FONT_DEFAULT.descriptor]
    Scene2DSkin.defaultSkin = skin(atlas) { skin ->
        createLabelStyles(bigFont, defaultFont)
        createImageButtonStyles(skin)
    }
}

private fun @SkinDsl Skin.createImageButtonStyles(skin: Skin) {
    imageButton(SkinImageButton.PAUSE_PLAY.name) {
        imageUp = skin.getDrawable(SkinImage.PAUSE.atlasKey)
        imageChecked = skin.getDrawable(SkinImage.PLAY.atlasKey)
        imageDown = imageChecked
    }
    imageButton(SkinImageButton.QUIT.name) {
        imageDown = skin.getDrawable(SkinImage.QUIT.atlasKey)
        imageUp = imageDown
    }
}

private fun @SkinDsl Skin.createLabelStyles(
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
