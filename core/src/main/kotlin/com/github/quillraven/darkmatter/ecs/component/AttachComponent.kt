package com.github.quillraven.darkmatter.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import ktx.math.vec2

class AttachComponent : Component, Pool.Poolable {
    lateinit var entity: Entity
    val offset = vec2()

    override fun reset() {
        offset.set(Vector2.Zero)
    }

    companion object {
        val mapper = mapperFor<AttachComponent>()
    }
}
