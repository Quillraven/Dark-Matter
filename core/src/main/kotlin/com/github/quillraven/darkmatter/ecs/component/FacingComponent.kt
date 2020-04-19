package com.github.quillraven.darkmatter.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class FacingComponent : Component, Pool.Poolable {
    var direction = FacingDirection.DEFAULT

    override fun reset() {
        direction = FacingDirection.DEFAULT
    }

    companion object {
        val mapper = mapperFor<FacingComponent>()
    }
}

enum class FacingDirection {
    LEFT, DEFAULT, RIGHT
}
