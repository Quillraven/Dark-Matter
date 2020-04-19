package com.github.quillraven.darkmatter.ui

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.github.quillraven.darkmatter.asset.BitmapFontAsset
import com.github.quillraven.darkmatter.asset.TextureAtlasAsset
import ktx.assets.async.AssetStorage
import ktx.scene2d.Scene2DSkin
import ktx.style.SkinDsl
import ktx.style.label
import ktx.style.skin

enum class LabelStyle {
    LARGE, DEFAULT
}

enum class UIImage(val atlasKey: String) {
    GAME_HUD("game_hud"),
    WARNING("warning"),
    LIFE_BAR("life_bar"),
    SHIELD_BAR("shield_bar")
}

fun createSkin(assets: AssetStorage) {
    val atlas = assets[TextureAtlasAsset.UI.descriptor]
    val bigFont = assets[BitmapFontAsset.FONT_LARGE_GRADIENT.descriptor]
    val defaultFont = assets[BitmapFontAsset.FONT_DEFAULT.descriptor]
    Scene2DSkin.defaultSkin = skin(atlas) {
        createLabels(bigFont, defaultFont)
    }
}

private fun @SkinDsl Skin.createLabels(
    bigFont: BitmapFont,
    defaultFont: BitmapFont
) {
    label(LabelStyle.LARGE.name) {
        font = bigFont
    }
    label(LabelStyle.DEFAULT.name) {
        font = defaultFont
    }
}
