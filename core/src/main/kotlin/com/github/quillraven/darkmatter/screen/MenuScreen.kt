package com.github.quillraven.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.github.quillraven.darkmatter.Game
import com.github.quillraven.darkmatter.PREFERENCE_HIGHSCORE_KEY
import com.github.quillraven.darkmatter.PREFERENCE_MUSIC_ENABLED_KEY
import com.github.quillraven.darkmatter.asset.MusicAsset
import com.github.quillraven.darkmatter.ecs.createDarkMatter
import com.github.quillraven.darkmatter.ecs.createPlayer
import com.github.quillraven.darkmatter.ecs.system.MoveSystem
import com.github.quillraven.darkmatter.ecs.system.PlayerAnimationSystem
import com.github.quillraven.darkmatter.ecs.system.PowerUpSystem
import com.github.quillraven.darkmatter.ui.ConfirmDialog
import com.github.quillraven.darkmatter.ui.MenuUI
import com.github.quillraven.darkmatter.ui.TextDialog
import ktx.actors.onChangeEvent
import ktx.actors.onClick
import ktx.actors.plusAssign
import ktx.ashley.getSystem
import ktx.preferences.flush
import ktx.preferences.get
import ktx.preferences.set

private const val PLAYER_SPAWN_Y = 3f

class MenuScreen(game: Game) : Screen(game, MusicAsset.MENU) {
    private val preferences = game.preferences
    private val ui = MenuUI(bundle).apply {
        startGameButton.onClick { game.setScreen<GameScreen>() }
        soundButton.onChangeEvent { _, actor ->
            audioService.enabled = !actor.isChecked
            preferences.flush {
                this[PREFERENCE_MUSIC_ENABLED_KEY] = audioService.enabled
            }
        }
        controlButton.onClick {
            controlsDialog.show(stage)
        }
        creditsButton.onClick {
            creditsDialog.show(stage)
        }
        quitGameButton.onClick {
            confirmDialog.show(stage)
        }
    }
    private val confirmDialog = ConfirmDialog(bundle).apply {
        yesButton.onClick { Gdx.app.exit() }
        noButton.onClick { hide() }
    }
    private val creditsDialog = TextDialog(bundle, "credits")
    private val controlsDialog = TextDialog(bundle, "controls")

    override fun show() {
        super.show()
        engine.run {
            getSystem<PowerUpSystem>().setProcessing(false)
            getSystem<MoveSystem>().setProcessing(false)
            getSystem<PlayerAnimationSystem>().setProcessing(false)
            createPlayer(assets, spawnY = PLAYER_SPAWN_Y)
            createDarkMatter()
        }

        audioService.enabled = preferences[PREFERENCE_MUSIC_ENABLED_KEY, true]

        setupUI()
    }

    private fun setupUI() {
        ui.run {
            soundButton.isChecked = !audioService.enabled
            updateHighScore(preferences[PREFERENCE_HIGHSCORE_KEY, 0])
            stage += ui.table
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
