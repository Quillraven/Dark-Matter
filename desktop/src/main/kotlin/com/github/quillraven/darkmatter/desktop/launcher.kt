package com.github.quillraven.darkmatter.lwjgl3.com.github.quillraven.darkmatter.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.github.quillraven.darkmatter.Game

fun main() {
    Lwjgl3Application(Game(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("Dark Matter")
        setWindowSizeLimits(640, 360, -1, -1)
        setWindowedMode(640, 360)
        setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png")
    })
}
