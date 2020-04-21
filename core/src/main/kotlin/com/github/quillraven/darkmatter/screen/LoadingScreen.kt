package com.github.quillraven.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.darkmatter.Game
import com.github.quillraven.darkmatter.asset.ShaderProgramAsset
import com.github.quillraven.darkmatter.asset.SoundAsset
import com.github.quillraven.darkmatter.asset.TextureAsset
import com.github.quillraven.darkmatter.asset.TextureAtlasAsset
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.log.logger

private val LOG = logger<LoadingScreen>()

class LoadingScreen(
    private val game: Game,
    private val stage: Stage = game.stage,
    private val assets: AssetStorage = game.assets
) : KtxScreen {
    override fun show() {
        LOG.debug { "Show" }

        val old = System.currentTimeMillis()
        val assetRefs = listOf(
            TextureAtlasAsset.values().filter { !it.isSkinAtlas }.map { assets.loadAsync(it.descriptor) },
            TextureAsset.values().map { assets.loadAsync(it.descriptor) },
            SoundAsset.values().map { assets.loadAsync(it.descriptor) },
            ShaderProgramAsset.values().map { assets.loadAsync(it.descriptor) }
        ).flatten()
        KtxAsync.launch {
            assetRefs.joinAll()
            LOG.debug { "It took ${(System.currentTimeMillis() - old) * 0.001f} seconds to load assets and initialize" }
            assetsLoaded()
        }
    }

    private fun assetsLoaded() {
        game.addScreen(GameScreen(game))
        game.addScreen(GameOverScreen(game))
        game.addScreen(MenuScreen(game))
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        if (assets.progress.isFinished && Gdx.input.justTouched() && game.containsScreen<MenuScreen>()) {
            game.removeScreen(LoadingScreen::class.java)
            dispose()
            game.setScreen<MenuScreen>()
        }

        stage.run {
            viewport.apply()
            act()
            draw()
        }
    }

    override fun dispose() {
        LOG.debug { "Dispose" }
    }
}
