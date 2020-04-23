package com.github.quillraven.darkmatter.screen

import com.badlogic.gdx.Gdx
import com.github.quillraven.darkmatter.Game
import com.github.quillraven.darkmatter.asset.MusicAsset

class MenuScreen(game: Game) : Screen(game, MusicAsset.MENU) {
    override fun render(delta: Float) {
        if (Gdx.input.justTouched()) game.setScreen<GameScreen>()
    }
}
