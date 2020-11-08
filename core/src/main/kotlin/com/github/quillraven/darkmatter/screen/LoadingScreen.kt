package com.github.quillraven.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn
import com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut
import com.badlogic.gdx.scenes.scene2d.actions.Actions.forever
import com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import com.github.quillraven.darkmatter.Game
import com.github.quillraven.darkmatter.asset.I18NBundleAsset
import com.github.quillraven.darkmatter.asset.ShaderProgramAsset
import com.github.quillraven.darkmatter.asset.SoundAsset
import com.github.quillraven.darkmatter.asset.TextureAsset
import com.github.quillraven.darkmatter.asset.TextureAtlasAsset
import com.github.quillraven.darkmatter.ui.SkinImage
import com.github.quillraven.darkmatter.ui.SkinLabel
import com.github.quillraven.darkmatter.ui.SkinTextButton
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.actors.plus
import ktx.actors.plusAssign
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf
import ktx.log.debug
import ktx.log.logger
import ktx.scene2d.actors
import ktx.scene2d.image
import ktx.scene2d.label
import ktx.scene2d.stack
import ktx.scene2d.table
import ktx.scene2d.textButton

private val LOG = logger<LoadingScreen>()
private const val ACTOR_FADE_IN_TIME = 0.5f
private const val ACTOR_FADE_OUT_TIME = 1f
private const val OFFSET_TITLE_Y = 15f
private const val ELEMENT_PADDING = 7f
private const val MENU_ELEMENT_OFFSET_TITLE_Y = 20f

class LoadingScreen(
    private val game: Game,
    private val stage: Stage = game.stage,
    private val assets: AssetStorage = game.assets
) : KtxScreen {
    private val bundle = assets[I18NBundleAsset.DEFAULT.descriptor]
    private lateinit var progressBar: Image
    private lateinit var progressText: TextButton
    private lateinit var touchToBegin: Label

    override fun show() {
        LOG.debug { "Show" }

        val old = System.currentTimeMillis()
        val assetRefs = gdxArrayOf(
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

        setupUI()
    }

    override fun hide() {
        stage.clear()
    }

    private fun setupUI() {
        stage.actors {
            table {
                defaults().fillX().expandX()

                label(bundle["gameTitle"], SkinLabel.LARGE.name) { cell ->
                    wrap = true
                    setAlignment(Align.center)
                    cell.apply {
                        padTop(OFFSET_TITLE_Y)
                        padBottom(MENU_ELEMENT_OFFSET_TITLE_Y)
                    }
                }
                row()

                touchToBegin = label(bundle["touchToBegin"], SkinLabel.LARGE.name) { cell ->
                    wrap = true
                    setAlignment(Align.center)
                    color.a = 0f
                    cell.padLeft(ELEMENT_PADDING).padRight(ELEMENT_PADDING).top().expandY()
                }
                row()

                stack { cell ->
                    progressBar = image(SkinImage.LIFE_BAR.atlasKey).apply {
                        scaleX = 0f
                    }
                    progressText = textButton(bundle["loading"], SkinTextButton.LABEL_TRANSPARENT.name)
                    cell.padLeft(ELEMENT_PADDING).padRight(ELEMENT_PADDING).padBottom(ELEMENT_PADDING)
                }

                top()
                setFillParent(true)
                pack()
            }
        }
    }

    private fun assetsLoaded() {
        game.addScreen(GameScreen(game))
        game.addScreen(GameOverScreen(game))
        game.addScreen(MenuScreen(game))
        touchToBegin += forever(sequence(fadeIn(ACTOR_FADE_IN_TIME) + fadeOut(ACTOR_FADE_OUT_TIME)))
        progressText.label.setText(bundle["loaded"])
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

        progressBar.scaleX = assets.progress.percent
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
