package com.github.quillraven.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.github.quillraven.darkmatter.Game
import com.github.quillraven.darkmatter.asset.MusicAsset
import com.github.quillraven.darkmatter.ecs.createDarkMatter
import com.github.quillraven.darkmatter.ecs.createPlayer
import com.github.quillraven.darkmatter.ecs.system.MoveSystem
import com.github.quillraven.darkmatter.ecs.system.PlayerAnimationSystem
import com.github.quillraven.darkmatter.ecs.system.PowerUpSystem
import com.github.quillraven.darkmatter.ui.ConfirmDialog
import com.github.quillraven.darkmatter.ui.MenuUI
import ktx.actors.onChangeEvent
import ktx.actors.onClick
import ktx.actors.plusAssign
import ktx.ashley.getSystem

private const val PLAYER_SPAWN_Y = 3f

class MenuScreen(game: Game) : Screen(game, MusicAsset.MENU) {
    private val ui = MenuUI(bundle).apply {
        startGameButton.onClick { game.setScreen<GameScreen>() }
        soundButton.onChangeEvent { _, actor -> audioService.enabled = !actor.isChecked }
        quitGameButton.onClick {
            confirmDialog.show(stage)
        }
    }
    private val confirmDialog = ConfirmDialog(bundle).apply {
        yesButton.onClick { Gdx.app.exit() }
        noButton.onClick { hide() }
    }

    override fun show() {
        super.show()
        engine.run {
            getSystem<PowerUpSystem>().setProcessing(false)
            getSystem<MoveSystem>().setProcessing(false)
            getSystem<PlayerAnimationSystem>().setProcessing(false)
            createPlayer(assets, spawnY = PLAYER_SPAWN_Y)
            createDarkMatter()
        }

        setupUI()
    }

    private fun setupUI() {
        ui.run {
            soundButton.isChecked = !audioService.enabled
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
