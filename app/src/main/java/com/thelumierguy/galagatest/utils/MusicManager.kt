package com.thelumierguy.galagatest.utils

import android.content.Context
import android.hardware.SensorManager
import android.media.MediaPlayer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.thelumierguy.galagatest.R

class MusicManager(val context: Context) : LifecycleObserver {

    private var mediaPlayer: MediaPlayer? = null


    private fun startMusic() {
        mediaPlayer = MediaPlayer.create(context, R.raw.music)
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
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun releaseComponents() {
        mediaPlayer?.release()
    }
}