package com.github.quillraven.darkmatter.audio

import com.badlogic.gdx.audio.Sound
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync

enum class SoundAsset(
    fileName: String,
    directory: String = "sound",
    val filePath: String = "$directory/$fileName"
) {
    BOOST_1("boost1.wav"),
    BOOST_2("boost2.wav"),
    EXPLOSION("explosion.wav"),
    LIFE("life.wav"),
    SHIELD("shield.wav")
}

interface AudioService {
    fun play(soundAsset: SoundAsset) = Unit
}

class DefaultAudioService(
    private val assets: AssetStorage,
    maxSimultaneousSounds: Int = 16
) : AudioService {
    private val soundChannel = Channel<SoundAsset>(maxSimultaneousSounds)

    init {
        KtxAsync.launch {
            soundChannel.consumeEach { soundAsset ->
                if (assets.isLoaded<Sound>(soundAsset.filePath)) {
                    assets.get<Sound>(soundAsset.filePath).play()
                }
            }
        }
    }

    override fun play(soundAsset: SoundAsset) {
        KtxAsync.launch {
            if (!assets.isLoaded<Sound>(soundAsset.filePath)) {
                assets.loadAsync<Sound>(soundAsset.filePath)
            }
            soundChannel.send(soundAsset)
        }
    }
}

