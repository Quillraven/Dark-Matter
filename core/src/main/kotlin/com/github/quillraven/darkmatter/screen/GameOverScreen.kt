package com.github.quillraven.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.github.quillraven.darkmatter.Game
import com.github.quillraven.darkmatter.asset.MusicAsset

class GameOverScreen(game: Game) : Screen(game, MusicAsset.GAME_OVER) {
    override fun render(delta: Float) {
        if (Gdx.input.justTouched()) game.setScreen<MenuScreen>()
    }
}
