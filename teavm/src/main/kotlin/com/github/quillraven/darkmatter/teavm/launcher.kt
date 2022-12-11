package com.github.quillraven.darkmatter.teavm

import com.github.quillraven.darkmatter.Game
import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration
import com.github.xpenatan.gdx.backends.teavm.TeaBuilder
import java.io.File

fun main() {
    val cfg = TeaBuildConfiguration().apply {
        assetsPath += (File("../assets"))
        webappPath = File("build/dist").canonicalPath
        obfuscate = true
        setApplicationListener(Game::class.java)
    }

    TeaBuilder.build(TeaBuilder.config(cfg).apply {
        mainClass = "com.github.quillraven.darkmatter.teavm.LauncherKt"
    })
}
