package com.github.quillraven.darkmatter.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

private const val DEFAULT_FRAME_DURATION = 1 / 20f

enum class AnimationType(
    val atlasKey: String,
    val playMode: Animation.PlayMode = Animation.PlayMode.LOOP,
    val speed: Float = 1f
) {
    NONE(""),
    FIRE("fire"),
    BOOST_1("orb_blue", speed = 0.5f),
    BOOST_2("orb_yellow", speed = 0.5f),
    LIFE("life"),
    SHIELD("shield", speed = 0.75f),
    DARK_MATTER("dark_matter", speed = 3f),
    EXPLOSION("explosion", Animation.PlayMode.NORMAL, speed = 0.5f)
}

class Animation2D(
    val type: AnimationType,
    keyFrames: Array<out TextureRegion>,
    playMode: PlayMode = PlayMode.LOOP,
    speed: Float = 1f
) : Animation<TextureRegion>((DEFAULT_FRAME_DURATION) / speed, keyFrames, playMode)

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
