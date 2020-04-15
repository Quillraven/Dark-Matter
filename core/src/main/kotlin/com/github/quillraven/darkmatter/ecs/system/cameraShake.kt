package com.github.quillraven.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool
import com.github.quillraven.darkmatter.event.GameEvent
import com.github.quillraven.darkmatter.event.GameEventListener
import com.github.quillraven.darkmatter.event.GameEventManager
import com.github.quillraven.darkmatter.event.GameEventType
import ktx.math.vec3

private const val MAX_SHAKE_INSTANCES = 4

private class CameraShake : Pool.Poolable {
    var maxDistortion = 0f // in world units
    var duration = 0f
    lateinit var camera: Camera
    private var storeCamPos = true
    private val origCamPosition = vec3()
    private var currentDuration = 0f

    fun update(deltaTime: Float): Boolean {
        if (storeCamPos) {
            // before starting the shake we need to store the current camera position
            // to reset it correctly when the shake is over
            storeCamPos = false
            origCamPosition.set(camera.position)
        }

        if (currentDuration < duration) {
            // currentPower represents a value between maxDistortion and 0.
            // At the beginning it is maxDistortion and decreases over time
            val currentPower = maxDistortion * ((duration - currentDuration) / duration)

            camera.position.x = origCamPosition.x + MathUtils.random(-1f, 1f) * currentPower
            camera.position.y = origCamPosition.y + MathUtils.random(-1f, 1f) * currentPower
            camera.update()

            currentDuration += deltaTime
            return false
        }

        // shake is over -> reset to original position
        camera.position.set(origCamPosition)
        camera.update()
        return true
    }

    override fun reset() {
        maxDistortion = 0f
        duration = 0f
        currentDuration = 0f
        origCamPosition.set(Vector3.Zero)
        storeCamPos = true
    }
}

private class CameraShakePool(private val camera: Camera) : Pool<CameraShake>(2, 4, false) {
    override fun newObject(): CameraShake {
        return CameraShake().apply {
            this.camera = this@CameraShakePool.camera
        }
    }
}

class CameraShakeSystem(
    camera: Camera,
    private val gameEventManager: GameEventManager
) : EntitySystem(), GameEventListener {
    private val shakePool = CameraShakePool(camera)
    private val activeShakes = Array<CameraShake>(MAX_SHAKE_INSTANCES)

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        gameEventManager.addListener(GameEventType.PLAYER_DAMAGED, this)
    }

    override fun removedFromEngine(engine: Engine?) {
        super.removedFromEngine(engine)
        gameEventManager.removeListener(GameEventType.PLAYER_DAMAGED, this)
    }

    override fun update(deltaTime: Float) {
        if (!activeShakes.isEmpty) {
            // update the first camera shake instance
            val shake = activeShakes.first()
            if (shake.update(deltaTime)) {
                // shake is over -> remove it from the pool
                activeShakes.removeIndex(0)
                shakePool.free(shake)
            }
        }
    }

    override fun onEvent(type: GameEventType, data: GameEvent?) {
        if (activeShakes.size < MAX_SHAKE_INSTANCES) {
            activeShakes.add(shakePool.obtain().apply {
                duration = 0.25f
                maxDistortion = 0.25f
            })
        }
    }
}
