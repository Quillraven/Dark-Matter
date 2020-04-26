package com.github.quillraven.darkmatter.ui

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import ktx.i18n.get
import ktx.scene2d.KTableWidget
import ktx.scene2d.imageButton
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton


private const val OFFSET_TITLE_Y = 15f
private const val MENU_ELEMENT_OFFSET_TITLE_Y = 20f
private const val MENU_DEFAULT_PADDING = 2.5f

class MenuUI(bundle: I18NBundle) {
    val table: KTableWidget
    val startGameButton: TextButton
    val soundButton: ImageButton
    val controlButton: TextButton
    val highScoreButton: TextButton
    val creditsButton: TextButton
    val quitGameButton: TextButton

    init {
        table = scene2d.table {
            defaults().pad(MENU_DEFAULT_PADDING).expandX().fillX().colspan(2)

            label(bundle["gameTitle"], SkinLabel.LARGE.name) { cell ->
                wrap = true
                setAlignment(Align.center)
                cell.apply {
                    padTop(OFFSET_TITLE_Y)
                    padBottom(MENU_ELEMENT_OFFSET_TITLE_Y)
                }
            }
            row()

            startGameButton = textButton(bundle["startGame"], SkinTextButton.DEFAULT.name) {
                label.wrap = true
            }
            row()

            soundButton = imageButton(SkinImageButton.SOUND_ON_OFF.name).cell(colspan = 1, expandX = false)
            controlButton = textButton(bundle["control"], SkinTextButton.DEFAULT.name) { cell ->
                label.wrap = true
                cell.colspan(1)
            }
            row()

            highScoreButton = textButton(bundle["highscore", 0], SkinLabel.DEFAULT.name) {
                label.wrap = true
            }
            row()

            creditsButton = textButton(bundle["credit"], SkinLabel.DEFAULT.name) {
                label.wrap = true
            }
            row()

            quitGameButton = textButton(bundle["quitGame"], SkinLabel.DEFAULT.name) {
                label.wrap = true
            }

            setFillParent(true)
            top()
            pack()
        }
    }
}
