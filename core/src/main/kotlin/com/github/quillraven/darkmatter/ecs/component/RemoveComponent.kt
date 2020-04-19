package com.github.quillraven.darkmatter.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class RemoveComponent : Component, Pool.Poolable {
    var delay = 0f

    override fun reset() {
        delay = 0f
    }

    companion object {
        val mapper = mapperFor<RemoveComponent>()
    }
}
