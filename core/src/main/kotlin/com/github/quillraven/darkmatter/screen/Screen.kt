package com.github.quillraven.darkmatter.screen

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.darkmatter.Game
import com.github.quillraven.darkmatter.asset.I18NBundleAsset
import com.github.quillraven.darkmatter.asset.MusicAsset
import com.github.quillraven.darkmatter.audio.AudioService
import com.github.quillraven.darkmatter.event.GameEvent
import com.github.quillraven.darkmatter.event.GameEventListener
import com.github.quillraven.darkmatter.event.GameEventManager
import kotlinx.coroutines.launch
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.log.debug
import ktx.log.logger
import java.lang.System.currentTimeMillis

private val LOG = logger<Screen>()

abstract class Screen(
    val game: Game,
    private val musicAsset: MusicAsset
) : KtxScreen, GameEventListener {
    private val gameViewport: Viewport = game.gameViewport
    val stage: Stage = game.stage
    val audioService: AudioService = game.audioService
    val engine: Engine = game.engine
    val gameEventManager: GameEventManager = game.gameEventManager
    val assets: AssetStorage = game.assets
    val bundle = assets[I18NBundleAsset.DEFAULT.descriptor]

    override fun show() {
        LOG.debug { "Show ${this::class.simpleName}" }
        val old = currentTimeMillis()
        val music = assets.loadAsync(musicAsset.descriptor)
        KtxAsync.launch {
            music.join()
            if (assets.isLoaded(musicAsset.descriptor)) {
                // music was really loaded and did not get unloaded already by the hide function
                LOG.debug { "It took ${(currentTimeMillis() - old) * 0.001f} seconds to load the $musicAsset music" }
                audioService.play(musicAsset)
            }
        }
    }

    override fun hide() {
        LOG.debug { "Hide ${this::class.simpleName}" }
        stage.clear()
        audioService.stop()
        LOG.debug { "Number of entities: ${engine.entities.size()}" }
        engine.removeAllEntities()
        gameEventManager.removeListener(this)
        KtxAsync.launch {
            assets.unload(musicAsset.descriptor)
        }
    }

    override fun resize(width: Int, height: Int) {
        gameViewport.update(width, height, true)
        stage.viewport.update(width, height, true)
    }

    override fun onEvent(event: GameEvent) = Unit
}
