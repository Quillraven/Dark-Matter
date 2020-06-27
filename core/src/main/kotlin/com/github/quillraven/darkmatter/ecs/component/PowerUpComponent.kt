package com.github.quillraven.darkmatter.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.github.quillraven.darkmatter.asset.SoundAsset
import ktx.ashley.mapperFor

enum class PowerUpType(
    val animationType: AnimationType,
    val collectSound: SoundAsset,
    val lifeGain: Float = 0f,
    val shieldGain: Float = 0f,
    val speedGain: Float = 0f
) {
    NONE(AnimationType.NONE, SoundAsset.EXPLOSION),
    SPEED_1(AnimationType.BOOST_1, SoundAsset.BOOST_1, speedGain = 3f),
    SPEED_2(AnimationType.BOOST_2, SoundAsset.BOOST_2, speedGain = 3.75f),
    LIFE(AnimationType.LIFE, SoundAsset.LIFE, lifeGain = 25f),
    SHIELD(AnimationType.SHIELD, SoundAsset.SHIELD, shieldGain = 25f)
}

class PowerUpComponent : Component, Pool.Poolable {
    var type = PowerUpType.NONE

    override fun reset() {
        type = PowerUpType.NONE
    }

    companion object {
        val mapper = mapperFor<PowerUpComponent>()
    }
}
