package com.thelumierguy.astroadventures.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_MUSIC
import android.media.SoundPool
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class SoundManager(
    val context: Context,
    private val soundData: SoundData,
    private val lifecycle: Lifecycle,
) : LifecycleObserver {

    var soundId: Int? = null

    init {
        lifecycle.addObserver(this)
    }

    private var soundPool: SoundPool? = null

    fun init() {
        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(AudioAttributes.Builder().setContentType(CONTENT_TYPE_MUSIC)
                .build())
            .build()
        soundId = soundPool?.load(context, soundData.soundFile, 1)

    }

    fun play() {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            soundId?.let { soundPool?.play(it, 1F, 1F, 1, 0, 1f) }
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopPlayback() {
        soundId?.let { soundid -> soundPool?.stop(soundid) }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}

data class SoundData(val soundFile: Int, val soundName: String)

const val PLAYER_BULLET_SOUND = "PLAYER_BULLET_SOUND"