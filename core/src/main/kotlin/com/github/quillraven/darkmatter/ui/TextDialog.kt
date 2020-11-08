package com.github.quillraven.darkmatter.ui

import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import com.github.quillraven.darkmatter.V_HEIGHT_PIXELS
import com.github.quillraven.darkmatter.V_WIDTH_PIXELS
import ktx.actors.onClick
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.scrollPane
import ktx.scene2d.textButton

private const val ELEMENT_PADDING = 7f
private const val DIALOG_WIDTH_SCALE = 0.95f
private const val DIALOG_HEIGHT_SCALE = 0.75f
private const val SCROLL_PANE_PAD_RIGHT = -7f // align with background frame border

class TextDialog(
    bundle: I18NBundle,
    txtBundleKey: String
) : Dialog("", Scene2DSkin.defaultSkin, SkinWindow.DEFAULT.name) {
    init {
        buttonTable.defaults().padBottom(ELEMENT_PADDING)
        button(scene2d.textButton(bundle["close"], SkinTextButton.DEFAULT.name).apply {
            labelCell.padLeft(ELEMENT_PADDING).padRight(ELEMENT_PADDING)
            onClick { hide() }
        })
        buttonTable.pack()

        contentTable.defaults().fill().expand()
        contentTable.add(scene2d.scrollPane(SkinScrollPane.DEFAULT.name) {
            setScrollbarsVisible(true)
            fadeScrollBars = false
            variableSizeKnobs = false

            label(bundle[txtBundleKey], SkinLabel.DEFAULT.name) {
                wrap = true
                setAlignment(Align.topLeft)
            }
        }).padRight(SCROLL_PANE_PAD_RIGHT)
        contentTable.pack()

        // we don't scale or rotate the dialog -> set transform to false to
        // avoid additional texture bindings and draw calls
        isTransform = false
    }

    override fun getPrefWidth() = V_WIDTH_PIXELS * DIALOG_WIDTH_SCALE

    override fun getPrefHeight() = V_HEIGHT_PIXELS * DIALOG_HEIGHT_SCALE
}
