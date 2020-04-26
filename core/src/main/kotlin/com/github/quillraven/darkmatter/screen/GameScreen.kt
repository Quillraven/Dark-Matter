package com.github.quillraven.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.github.quillraven.darkmatter.Game
import com.github.quillraven.darkmatter.PREFERENCE_HIGHSCORE_KEY
import com.github.quillraven.darkmatter.asset.I18NBundleAsset
import com.github.quillraven.darkmatter.asset.MusicAsset
import com.github.quillraven.darkmatter.asset.SoundAsset.SPAWN
import com.github.quillraven.darkmatter.ecs.PLAYER_START_SPEED
import com.github.quillraven.darkmatter.ecs.component.MAX_LIFE
import com.github.quillraven.darkmatter.ecs.component.MAX_SHIELD
import com.github.quillraven.darkmatter.ecs.component.PlayerComponent
import com.github.quillraven.darkmatter.ecs.component.PowerUpType
import com.github.quillraven.darkmatter.ecs.createDarkMatter
import com.github.quillraven.darkmatter.ecs.createPlayer
import com.github.quillraven.darkmatter.ecs.system.PowerUpSystem
import com.github.quillraven.darkmatter.ecs.system.RenderSystem
import com.github.quillraven.darkmatter.event.GameEvent
import com.github.quillraven.darkmatter.event.GameEventListener
import com.github.quillraven.darkmatter.event.GameEventPlayerBlock
import com.github.quillraven.darkmatter.event.GameEventPlayerDeath
import com.github.quillraven.darkmatter.event.GameEventPlayerHit
import com.github.quillraven.darkmatter.event.GameEventPlayerMove
import com.github.quillraven.darkmatter.event.GameEventPowerUp
import com.github.quillraven.darkmatter.event.GameEventType
import com.github.quillraven.darkmatter.event.GameEventType.PLAYER_SPAWN
import com.github.quillraven.darkmatter.ui.GameUI
import ktx.actors.onChangeEvent
import ktx.actors.onClick
import ktx.actors.plusAssign
import ktx.ashley.get
import ktx.ashley.getSystem
import ktx.log.debug
import ktx.log.logger
import ktx.preferences.flush
import ktx.preferences.get
import ktx.preferences.set
import kotlin.math.min
import kotlin.math.roundToInt

private val LOG = logger<GameScreen>()
private const val MAX_DELTA_TIME = 1 / 30f

class GameScreen(game: Game) : Screen(game, MusicAsset.GAME), GameEventListener {
    private val ui = GameUI(assets[I18NBundleAsset.DEFAULT.descriptor]).apply {
        quitImageButton.onClick {
            game.setScreen<MenuScreen>()
        }
        pauseResumeButton.onChangeEvent { _, actor ->
            when (actor.isChecked) {
                true -> audioService.pause()
                else -> audioService.resume()
            }
        }
    }
    private val renderSystem = game.engine.getSystem<RenderSystem>()
    private val preferences = game.preferences

    override fun show() {
        super.show()
        gameEventManager.run {
            addListener(PLAYER_SPAWN, this@GameScreen)
            addListener(GameEventType.PLAYER_DEATH, this@GameScreen)
            addListener(GameEventType.PLAYER_MOVE, this@GameScreen)
            addListener(GameEventType.PLAYER_HIT, this@GameScreen)
            addListener(GameEventType.POWER_UP, this@GameScreen)
            addListener(GameEventType.PLAYER_BLOCK, this@GameScreen)
        }
        engine.run {
            createPlayer(assets)
            audioService.play(SPAWN)
            gameEventManager.dispatchEvent(PLAYER_SPAWN)
            createDarkMatter()

            // remove any power ups and reset the spawn timer
            getSystem<PowerUpSystem>().reset()
        }
        setupUI()
    }

    private fun setupUI() {
        ui.run {
            // reset to initial values
            updateDistance(0f)
            updateSpeed(PLAYER_START_SPEED)
            updateLife(
                MAX_LIFE,
                MAX_LIFE
            )
            updateShield(0f, MAX_SHIELD)

            // disable pauseResume button until game was started
            pauseResumeButton.run {
                this.touchable = Touchable.disabled
                this.isChecked = false
            }
            touchToBeginLabel.isVisible = true
        }
        stage += ui
    }

    override fun render(delta: Float) {
        if (Gdx.input.justTouched() && ui.touchToBeginLabel.isVisible) {
            ui.touchToBeginLabel.isVisible = false
            ui.pauseResumeButton.touchable = Touchable.enabled
        }

        val deltaTime = min(delta, MAX_DELTA_TIME)
        if (ui.pauseResumeButton.isChecked || ui.touchToBeginLabel.isVisible) {
            renderSystem.update(0f)
        } else {
            engine.update(deltaTime)
            audioService.update()
        }

        // render UI
        stage.run {
            viewport.apply()
            act(deltaTime)
            draw()
        }
    }

    override fun onEvent(type: GameEventType, data: GameEvent?) {
        when (type) {
            PLAYER_SPAWN -> {
                LOG.debug { "Spawn new player" }
                ui.updateDistance(0f)
            }
            GameEventType.PLAYER_DEATH -> {
                val distance = (data as GameEventPlayerDeath).distance.roundToInt()
                LOG.debug { "Player died with a distance of $distance" }
                if (distance > preferences[PREFERENCE_HIGHSCORE_KEY, 0]) {
                    preferences.flush {
                        this[PREFERENCE_HIGHSCORE_KEY] = distance
                    }
                }
                game.setScreen<GameOverScreen>()
            }
            GameEventType.PLAYER_MOVE -> {
                ui.run {
                    updateDistance((data as GameEventPlayerMove).distance)
                    updateSpeed(data.speed)
                }
            }
            GameEventType.PLAYER_HIT -> {
                ui.run {
                    updateLife((data as GameEventPlayerHit).life, data.maxLife)
                    showWarning()
                }
            }
            GameEventType.POWER_UP -> {
                val eventData = data as GameEventPowerUp
                data.player[PlayerComponent.mapper]?.let { player ->
                    when (eventData.type) {
                        PowerUpType.LIFE -> ui.updateLife(player.life, player.maxLife)
                        PowerUpType.SHIELD -> ui.updateShield(player.shield, player.maxShield)
                        else -> {
                            // ignore
                        }
                    }
                }
            }
            GameEventType.PLAYER_BLOCK -> {
                ui.updateShield((data as GameEventPlayerBlock).shield, data.maxShield)
            }
        }
    }
}
