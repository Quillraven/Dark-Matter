package com.github.quillraven.darkmatter.ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import com.github.quillraven.darkmatter.ecs.component.AttachComponent
import com.github.quillraven.darkmatter.ecs.component.RemoveComponent
import com.github.quillraven.darkmatter.ecs.component.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get

class AttachSystem :
    EntityListener,
    IteratingSystem(allOf(AttachComponent::class, TransformComponent::class).exclude(RemoveComponent::class).get()) {

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity[AttachComponent.mapper]?.let { attach ->
            entity[TransformComponent.mapper]?.let { transform ->
                attach.entity[TransformComponent.mapper]?.let { attachTransform ->
                    transform.position.set(
                        attachTransform.position.x + attach.offset.x,
                        attachTransform.position.y + attach.offset.y,
                        transform.position.z
                    )
                }
            }
        }
    }

    override fun entityRemoved(entity: Entity) {
        entities.forEach {
            it[AttachComponent.mapper]?.let { attach ->
                if (attach.entity == entity) {
                    it.add(engine.createComponent(RemoveComponent::class.java))
                }
            }
        }
    }

    override fun entityAdded(entity: Entity) = Unit
}
