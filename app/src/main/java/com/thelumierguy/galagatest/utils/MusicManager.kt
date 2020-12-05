package com.thelumierguy.galagatest.utils

import android.content.Context
import android.hardware.SensorManager
import android.media.MediaPlayer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.thelumierguy.galagatest.R

class MusicManager(context: Context) : LifecycleObserver {

    private var mediaPlayer: MediaPlayer? = null

    init {
        mediaPlayer = MediaPlayer.create(context, R.raw.music)
    }

    private fun startMusic() {
        mediaPlayer?.setOnPreparedListener {
            it.start()
        }
        mediaPlayer?.setOnCompletionListener {
            it.start()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun startListening() {
        startMusic()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopListening() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }
}