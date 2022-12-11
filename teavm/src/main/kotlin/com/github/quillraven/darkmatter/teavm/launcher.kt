package com.github.quillraven.darkmatter.teavm

import com.github.quillraven.darkmatter.Game
import com.github.quillraven.darkmatter.ecs.component.*
import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration
import com.github.xpenatan.gdx.backends.teavm.TeaBuilder
import com.github.xpenatan.gdx.backends.teavm.plugins.TeaReflectionSupplier
import java.io.File

fun main() {
    val cfg = TeaBuildConfiguration().apply {
        assetsPath += (File("../assets"))
        webappPath = File("build/dist").canonicalPath
        obfuscate = true
        htmlWidth = 360
        htmlHeight = 640

        setApplicationListener(Game::class.java)
    }

    TeaReflectionSupplier.addReflectionClass(AnimationComponent::class.java)
    TeaReflectionSupplier.addReflectionClass(AttachComponent::class.java)
    TeaReflectionSupplier.addReflectionClass(FacingComponent::class.java)
    TeaReflectionSupplier.addReflectionClass(GraphicComponent::class.java)
    TeaReflectionSupplier.addReflectionClass(MoveComponent::class.java)
    TeaReflectionSupplier.addReflectionClass(PlayerComponent::class.java)
    TeaReflectionSupplier.addReflectionClass(PowerUpComponent::class.java)
    TeaReflectionSupplier.addReflectionClass(RemoveComponent::class.java)
    TeaReflectionSupplier.addReflectionClass(TransformComponent::class.java)

    TeaBuilder.build(TeaBuilder.config(cfg))
}
