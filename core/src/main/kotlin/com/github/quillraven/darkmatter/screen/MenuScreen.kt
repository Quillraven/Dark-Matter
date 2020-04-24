package com.github.quillraven.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Align
import com.github.quillraven.darkmatter.Game
import com.github.quillraven.darkmatter.asset.I18NBundleAsset
import com.github.quillraven.darkmatter.asset.MusicAsset
import com.github.quillraven.darkmatter.ecs.createDarkMatter
import com.github.quillraven.darkmatter.ecs.createPlayer
import com.github.quillraven.darkmatter.ecs.system.MoveSystem
import com.github.quillraven.darkmatter.ecs.system.PlayerAnimationSystem
import com.github.quillraven.darkmatter.ecs.system.PowerUpSystem
import com.github.quillraven.darkmatter.ui.SkinImageButton
import com.github.quillraven.darkmatter.ui.SkinLabel
import com.github.quillraven.darkmatter.ui.SkinTextButton
import ktx.actors.onChangeEvent
import ktx.actors.onClick
import ktx.ashley.getSystem
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.StageWidget
import ktx.scene2d.actors
import ktx.scene2d.imageButton
import ktx.scene2d.label
import ktx.scene2d.table
import ktx.scene2d.textButton

private const val PLAYER_SPAWN_Y = 3f
private const val OFFSET_TITLE_Y = 15f
private const val MENU_ELEMENT_OFFSET_TITLE_Y = 20f
private const val MENU_DEFAULT_PADDING = 5f

class MenuScreen(game: Game) : Screen(game, MusicAsset.MENU) {
    private val bundle = assets[I18NBundleAsset.DEFAULT.descriptor]
    private val highScoreText = "${bundle["highscore"]}: "

    override fun show() {
        super.show()
        engine.run {
            getSystem<PowerUpSystem>().setProcessing(false)
            getSystem<MoveSystem>().setProcessing(false)
            getSystem<PlayerAnimationSystem>().setProcessing(false)
            createPlayer(assets, spawnY = PLAYER_SPAWN_Y)
            createDarkMatter()
        }

        stage.actors {
            createMenuUI()
        }
    }

    private fun @Scene2dDsl StageWidget.createMenuUI() {
        table {
            defaults().pad(MENU_DEFAULT_PADDING)

            label(bundle["gameTitle"], SkinLabel.LARGE.name) {
                wrap = true
                setAlignment(Align.center)
            }.cell(expandX = true, fillX = true, padTop = OFFSET_TITLE_Y, padBottom = MENU_ELEMENT_OFFSET_TITLE_Y)
            row()

            textButton(bundle["startGame"], SkinTextButton.DEFAULT.name) {
                label.run {
                    wrap = true
                    setAlignment(Align.center)
                }
                onClick { game.setScreen<GameScreen>() }
            }.cell(expandX = true, fillX = true)
            row()

            imageButton(SkinImageButton.SOUND_ON_OFF.name) {
                this.isChecked = !audioService.enabled
                onChangeEvent { _, actor -> audioService.enabled = !actor.isChecked }
            }
            row()

            textButton("${highScoreText}0", SkinLabel.DEFAULT.name) {
                label.run {
                    wrap = true
                    setAlignment(Align.center)
                }
            }.cell(expandX = true, fillX = true)
            row()

            textButton(bundle["quitGame"], SkinLabel.DEFAULT.name) {
                label.run {
                    wrap = true
                    setAlignment(Align.center)
                }
                onClick { Gdx.app.exit() }
            }.cell(expandX = true, fillX = true)

            setFillParent(true)
            top()
            pack()
        }
    }

    override fun hide() {
        super.hide()
        engine.run {
            getSystem<PowerUpSystem>().setProcessing(true)
            getSystem<MoveSystem>().setProcessing(true)
            getSystem<PlayerAnimationSystem>().setProcessing(true)
        }
    }

    override fun render(delta: Float) {
        engine.update(delta)
        stage.run {
            viewport.apply()
            act(delta)
            draw()
        }
    }
}
