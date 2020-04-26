package com.github.quillraven.darkmatter.ui

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import ktx.i18n.get
import ktx.scene2d.KTableWidget
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton

private const val OFFSET_TITLE_Y = 15f
private const val MENU_ELEMENT_OFFSET_TITLE_Y = 20f
private const val MENU_DEFAULT_PADDING = 10f
private const val MAX_HIGHSCORE_DISPLAYED = 999

class GameOverUI(private val bundle: I18NBundle) {
    val table: KTableWidget
    private val lastScoreButton: TextButton
    private val highScoreButton: TextButton
    val backButton: TextButton

    init {
        table = scene2d.table {
            defaults().pad(MENU_DEFAULT_PADDING).expandX().fillX()

            label(bundle["gameTitle"], SkinLabel.LARGE.name) { cell ->
                wrap = true
                setAlignment(Align.center)
                cell.apply {
                    padTop(OFFSET_TITLE_Y)
                    padBottom(MENU_ELEMENT_OFFSET_TITLE_Y)
                }
            }
            row()

            lastScoreButton = textButton(bundle["score", 0], SkinLabel.DEFAULT.name) {
                label.wrap = true
            }
            row()

            highScoreButton = textButton(bundle["highscore", 0], SkinLabel.DEFAULT.name) {
                label.wrap = true
            }
            row()

            backButton = textButton(bundle["backToMenu"], SkinLabel.DEFAULT.name) {
                label.wrap = true
            }
            row()

            setFillParent(true)
            top()
            pack()
        }
    }

    fun updateScores(score: Int, highScore: Int) {
        lastScoreButton.label.run {
            text.setLength(0)
            text.append(bundle["score", MathUtils.clamp(score, 0, MAX_HIGHSCORE_DISPLAYED)])
            invalidateHierarchy()
        }
        highScoreButton.label.run {
            text.setLength(0)
            text.append(bundle["highscore", MathUtils.clamp(highScore, 0, MAX_HIGHSCORE_DISPLAYED)])
            invalidateHierarchy()
        }
    }
}
