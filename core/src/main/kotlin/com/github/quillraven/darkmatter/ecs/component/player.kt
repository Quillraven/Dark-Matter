package com.github.quillraven.darkmatter.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class PlayerComponent : Component, Pool.Poolable {
    var life = 100f
    var maxLife = 100f
    var shield = 0f
    var maxShield = 100f
    var distance = 0f

    override fun reset() {
        life = 100f
        maxLife = 100f
        shield = 0f
        maxShield = 100f
        distance = 0f
    }

    companion object {
        val mapper = mapperFor<PlayerComponent>()
    }
}
