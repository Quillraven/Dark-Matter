package com.github.quillraven.darkmatter.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

enum class AnimationType(
    val atlasKey: String,
    val playMode: Animation.PlayMode = Animation.PlayMode.LOOP
) {
    NONE(""),
    FIRE("fire", Animation.PlayMode.LOOP_PINGPONG),
    BOOST_1("orb_blue"),
    BOOST_2("orb_yellow"),
    LIFE("life"),
    SHIELD("shield")
}

class Animation2D(
    val type: AnimationType,
    keyFrames: Array<out TextureRegion>,
    playMode: PlayMode = PlayMode.LOOP
) : Animation<TextureRegion>(1 / 20f, keyFrames, playMode)

class AnimationComponent : Component, Pool.Poolable {
    var type = AnimationType.NONE
    var stateTime = 0f
    lateinit var animation: Animation2D

    override fun reset() {
        type = AnimationType.NONE
        stateTime = 0f
    }

    companion object {
        val mapper = mapperFor<AnimationComponent>()
    }
}
