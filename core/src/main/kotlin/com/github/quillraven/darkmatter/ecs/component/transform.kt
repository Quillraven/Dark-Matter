package com.github.quillraven.darkmatter.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import ktx.math.vec2
import ktx.math.vec3

class TransformComponent : Component, Pool.Poolable, Comparable<TransformComponent> {
    val position = vec3()
    val size = vec2(1f, 1f)
    var rotationDeg = 0f

    override fun reset() {
        position.set(Vector3.Zero)
        size.set(1f, 1f)
        rotationDeg = 0f
    }

    override fun compareTo(other: TransformComponent): Int {
        val zDiff = position.z - other.position.z
        return (if (zDiff == 0f) position.y - other.position.y else zDiff).toInt()
    }

    companion object {
        val mapper = mapperFor<TransformComponent>()

    }
}
