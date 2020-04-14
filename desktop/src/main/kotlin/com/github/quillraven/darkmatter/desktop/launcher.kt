package com.github.quillraven.darkmatter.lwjgl3.com.github.quillraven.darkmatter.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.math.Vector2
import com.github.quillraven.darkmatter.Game

fun main() {
    val primaryDisplay = Lwjgl3ApplicationConfiguration.getPrimaryMonitor()
    val mode = Lwjgl3ApplicationConfiguration.getDisplayMode(primaryDisplay)
    Lwjgl3Application(
        Game(Vector2(mode.width.toFloat(), mode.height.toFloat())),
        Lwjgl3ApplicationConfiguration().apply {
            setTitle("Dark Matter")
            setWindowSizeLimits(360, 640, -1, -1)
            setWindowedMode(360, 640)
            setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png")
        })
}
