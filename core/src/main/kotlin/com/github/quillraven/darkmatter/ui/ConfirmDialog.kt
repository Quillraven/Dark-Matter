package com.github.quillraven.darkmatter.ui

import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import com.github.quillraven.darkmatter.V_HEIGHT_PIXELS
import com.github.quillraven.darkmatter.V_WIDTH_PIXELS
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.textButton

private const val ELEMENT_PADDING = 7f
private const val DIALOG_WIDTH_SCALE = 0.85f
private const val DIALOG_HEIGHT_SCALE = 0.5f

class ConfirmDialog(bundle: I18NBundle) : Dialog("", Scene2DSkin.defaultSkin, SkinWindow.DEFAULT.name) {
    val yesButton = scene2d.textButton(bundle["yes"], SkinTextButton.DEFAULT.name) {
        labelCell.padLeft(ELEMENT_PADDING).padRight(ELEMENT_PADDING)
    }
    val noButton = scene2d.textButton(bundle["no"], SkinTextButton.DEFAULT.name) {
        labelCell.padLeft(ELEMENT_PADDING).padRight(ELEMENT_PADDING)
    }

    init {
        contentTable.defaults().fillX().expandX().pad(ELEMENT_PADDING)
        text(scene2d.label(bundle["areYouSure"], SkinLabel.DEFAULT.name).apply {
            setWrap(true)
            setAlignment(Align.center)
        })
        contentTable.pack()

        buttonTable.defaults().padBottom(ELEMENT_PADDING)
        button(yesButton)
        button(noButton)
        buttonTable.pack()

        // we don't scale or rotate the dialog -> set transform to false to
        // avoid additional texture bindings and draw calls
        isTransform = false
    }

    override fun getPrefWidth() = V_WIDTH_PIXELS * DIALOG_WIDTH_SCALE

    override fun getPrefHeight() = V_HEIGHT_PIXELS * DIALOG_HEIGHT_SCALE
}
