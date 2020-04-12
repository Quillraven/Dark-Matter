package com.github.quillraven.darkmatter.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import ktx.math.vec2

class MoveComponent : Component, Pool.Poolable {
    val speed = vec2()

    override fun reset() {
        speed.set(Vector2.Zero)
    }

    companion object {
        val mapper = mapperFor<MoveComponent>()
    }
}
