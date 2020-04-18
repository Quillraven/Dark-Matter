package com.github.quillraven.darkmatter.audio

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.utils.Pool
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.log.logger
import java.util.*

private val LOG = logger<AudioService>()
private const val MAX_SOUND_INSTANCES = 16

enum class SoundAsset(
    fileName: String,
    directory: String = "sound",
    val filePath: String = "$directory/$fileName"
) {
    NONE(""),
    BOOST_1("boost1.wav"),
    BOOST_2("boost2.wav"),
    EXPLOSION("explosion.wav"),
    LIFE("life.wav"),
    SHIELD("shield.wav")
}

interface AudioService {
    fun play(soundAsset: SoundAsset) = Unit
    fun update()
}

private class SoundRequest : Pool.Poolable {
    var soundAsset = SoundAsset.NONE
    var volume = 0f

    override fun reset() {
        soundAsset = SoundAsset.NONE
        volume = 0f
    }
}

private class SoundRequestPool : Pool<SoundRequest>() {
    override fun newObject() = SoundRequest()
}

class DefaultAudioService(private val assets: AssetStorage) : AudioService {
    private val soundCache = EnumMap<SoundAsset, Sound>(SoundAsset::class.java)
    private val soundRequestPool = SoundRequestPool()
    private val soundRequests = EnumMap<SoundAsset, SoundRequest>(SoundAsset::class.java)

    override fun play(soundAsset: SoundAsset) {
        if (soundRequests.size >= MAX_SOUND_INSTANCES) {
            LOG.debug { "Maximum sound instances reached" }
            return
        }

        if (soundAsset in soundRequests) {
            // TODO set request sound to maximum volume of both requests
            LOG.debug { "Duplicated sound request for sound $soundAsset" }
            return
        }

        LOG.debug { "Adding new sound request for sound $soundAsset. Free request objects: ${soundRequestPool.free}" }
        soundRequests[soundAsset] = soundRequestPool.obtain().apply {
            this.soundAsset = soundAsset
            this.volume = 1f
        }
        KtxAsync.launch {
            if (!assets.isLoaded<Sound>(soundAsset.filePath)) {
                LOG.debug { "Loading new sound asset of type $soundAsset" }
                val sound = assets.loadAsync<Sound>(soundAsset.filePath)
                soundCache.put(soundAsset, sound.await())
            }
        }
    }

    override fun update() {
        if (!soundRequests.isEmpty()) {
            LOG.debug { "Playing ${soundRequests.size} sounds" }
            soundRequests.values.forEach { request ->
                soundCache[request.soundAsset]?.play()
                soundRequestPool.free(request)
            }
            soundRequests.clear()
        }
    }
}

