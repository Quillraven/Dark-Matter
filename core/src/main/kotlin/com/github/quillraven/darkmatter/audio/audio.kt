package com.github.quillraven.darkmatter.audio

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.utils.Pool
import com.github.quillraven.darkmatter.asset.MusicAsset
import com.github.quillraven.darkmatter.asset.SoundAsset
import ktx.assets.async.AssetStorage
import ktx.log.logger
import java.util.*
import kotlin.math.max

private val LOG = logger<AudioService>()
private const val MAX_SOUND_INSTANCES = 16

interface AudioService {
    fun play(soundAsset: SoundAsset, volume: Float = 1f) = Unit
    fun play(musicAsset: MusicAsset, volume: Float = 1f, loop: Boolean = true) = Unit
    fun update() = Unit
}

private class SoundRequest : Pool.Poolable {
    lateinit var soundAsset: SoundAsset
    var volume = 1f

    override fun reset() {
        volume = 1f
    }
}

private class SoundRequestPool : Pool<SoundRequest>() {
    override fun newObject() = SoundRequest()
}

class DefaultAudioService(private val assets: AssetStorage) : AudioService {
    private val soundCache = EnumMap<SoundAsset, Sound>(SoundAsset::class.java)
    private val soundRequestPool = SoundRequestPool()
    private val soundRequests = EnumMap<SoundAsset, SoundRequest>(SoundAsset::class.java)

    override fun play(soundAsset: SoundAsset, volume: Float) {
        when {
            soundAsset in soundRequests -> {
                // same request multiple times in one frame -> set volume to maximum of both requests
                LOG.debug { "Duplicated sound request for sound $soundAsset" }
                soundRequests[soundAsset]?.let { request ->
                    request.volume = max(request.volume, volume)
                }
            }
            soundRequests.size >= MAX_SOUND_INSTANCES -> {
                // maximum simultaneous sound instances reached -> do nothing
                LOG.debug { "Maximum sound instances reached" }
            }
            else -> {
                // new request
                if (soundAsset.descriptor !in assets) {
                    // sound not loaded -> error
                    LOG.error { "Sound $soundAsset is not loaded" }
                    return
                } else if (soundAsset !in soundCache) {
                    // cache sound for faster access in the future
                    LOG.debug { "Adding sound $soundAsset to sound cache" }
                    soundCache[soundAsset] = assets[soundAsset.descriptor]
                }

                // get request instance from pool and add it to the queue
                LOG.debug { "New sound request for sound $soundAsset. Free request objects: ${soundRequestPool.free}" }
                soundRequests[soundAsset] = soundRequestPool.obtain().apply {
                    this.soundAsset = soundAsset
                    this.volume = volume
                }
            }
        }
    }

    override fun play(musicAsset: MusicAsset, volume: Float, loop: Boolean) {
        if (musicAsset.descriptor !in assets) {
            LOG.error { "Music $musicAsset is not loaded" }
            return
        }

        assets[musicAsset.descriptor].apply {
            this.volume = volume
            this.isLooping = loop
            play()
        }
    }

    override fun update() {
        if (!soundRequests.isEmpty()) {
            // there are sounds to be played
            LOG.debug { "Playing ${soundRequests.size} sound(s)" }
            soundRequests.values.forEach { request ->
                soundCache[request.soundAsset]?.play(request.volume)
                soundRequestPool.free(request)
            }
            soundRequests.clear()
        }
    }
}

