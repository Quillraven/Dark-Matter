package com.github.quillraven.darkmatter.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.github.quillraven.darkmatter.Game

fun main() {
    Lwjgl3Application(
        Game(),
        Lwjgl3ApplicationConfiguration().apply {
            setTitle("Dark Matter")
            setWindowSizeLimits(360, 640, -1, -1)
            setWindowedMode(360, 640)
            setWindowIcon("icon.png")
        })
}
