package com.github.quillraven.darkmatter

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.profiling.GLProfiler
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.github.quillraven.darkmatter.event.GameEventManager
import com.github.quillraven.darkmatter.screen.LoadingScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.log.logger

private val LOG = logger<Game>()
const val V_WIDTH_PIXELS = 90
const val V_HEIGHT_PIXELS = 160
const val V_WIDTH = 9
const val V_HEIGHT = 16
const val UNIT_SCALE = 1 / 8f

class Game(val screenResolution: Vector2) : KtxGame<KtxScreen>() {
    val batch: Batch by lazy { SpriteBatch() }
    val stage: Stage by lazy {
        Stage(FitViewport(V_WIDTH_PIXELS.toFloat(), V_HEIGHT_PIXELS.toFloat()), batch)
    }
    val assets: AssetStorage by lazy {
        KtxAsync.initiate()
        AssetStorage()
    }
    val gameEventManager by lazy { GameEventManager() }
    private val profiler by lazy { GLProfiler(Gdx.graphics) }

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        profiler.enable()

        if (screenResolution.isZero) {
            // game launched from android -> update with real screen resolution
            // we cannot use the same approach for desktop because width and height returns
            // the size of the window instead of the display size. Therefore, for desktop we
            // pass those values in from the outside
            screenResolution.set(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        }

        addScreen(LoadingScreen(this))
        setScreen<LoadingScreen>()
    }

    override fun render() {
        profiler.reset()
        super.render()
    }

    override fun dispose() {
        LOG.debug { "Dispose game with ${this.screens.size} screen(s)" }
        LOG.debug { "Last number of draw calls: ${profiler.drawCalls}" }
        LOG.debug { "Last number of texture bindings: ${profiler.textureBindings}" }
        super.dispose()
        batch.dispose()
        assets.dispose()
        stage.dispose()
    }
}
