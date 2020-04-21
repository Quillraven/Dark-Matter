package com.github.quillraven.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.darkmatter.Game
import com.github.quillraven.darkmatter.asset.MusicAsset
import com.github.quillraven.darkmatter.audio.AudioService
import kotlinx.coroutines.launch
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.log.logger

private val LOG = logger<GameOverScreen>()

class GameOverScreen(
    private val game: Game,
    private val assets: AssetStorage = game.assets,
    private val audioService: AudioService = game.audioService,
    private val stage: Stage = game.stage
) : KtxScreen {
    override fun show() {
        LOG.debug { "Show" }
        val music = assets.loadAsync(MusicAsset.GAME_OVER.descriptor)
        KtxAsync.launch {
            music.join()
            audioService.play(MusicAsset.GAME_OVER)
        }
    }

    override fun hide() {
        LOG.debug { "Hide" }
        audioService.stop()
        KtxAsync.launch {
            assets.unload(MusicAsset.GAME_OVER.descriptor)
        }
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        if (Gdx.input.justTouched()) game.setScreen<MenuScreen>()
    }
}
