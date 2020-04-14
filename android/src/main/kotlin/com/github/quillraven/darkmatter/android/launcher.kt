package com.github.quillraven.darkmatter.android

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.github.quillraven.darkmatter.Game

class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize(Game(), AndroidApplicationConfiguration().apply {
            hideStatusBar = true
            useImmersiveMode = true
        })
    }
}
