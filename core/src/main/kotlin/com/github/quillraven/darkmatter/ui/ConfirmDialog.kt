package com.github.quillraven.darkmatter.ui

import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import com.github.quillraven.darkmatter.V_HEIGHT_PIXELS
import com.github.quillraven.darkmatter.V_WIDTH_PIXELS
import ktx.scene2d.Scene2DSkin

private const val ELEMENT_PADDING = 7f
private const val DIALOG_WIDTH_SCALE = 0.75f
private const val DIALOG_HEIGHT_SCALE = 0.5f

class ConfirmDialog(bundle: I18NBundle) : Dialog("", Scene2DSkin.defaultSkin, SkinWindow.DEFAULT.name) {
    val yesButton = TextButton(" ${bundle["yes"]} ", Scene2DSkin.defaultSkin, SkinTextButton.DEFAULT.name)
    val noButton = TextButton(" ${bundle["no"]} ", Scene2DSkin.defaultSkin, SkinTextButton.DEFAULT.name)

    init {
        contentTable.defaults().fillX().expandX().pad(ELEMENT_PADDING)
        text(Label(bundle["areYouSure"], Scene2DSkin.defaultSkin, SkinLabel.DEFAULT.name).apply {
            wrap = true
            setAlignment(Align.center)
        })

        buttonTable.defaults().padBottom(ELEMENT_PADDING)
        button(yesButton)
        button(noButton)
        // we don't scale or rotate the dialog -> set transform to false to
        // avoid additional texture bindings and draw calls
        isTransform = false
    }

    override fun getPrefWidth() = V_WIDTH_PIXELS * DIALOG_WIDTH_SCALE

    override fun getPrefHeight() = V_HEIGHT_PIXELS * DIALOG_HEIGHT_SCALE
}
