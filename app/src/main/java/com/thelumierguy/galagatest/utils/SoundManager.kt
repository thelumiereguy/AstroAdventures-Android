package com.thelumierguy.galagatest.utils

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.IntegerRes

class SoundManager(private val soundFile: Int, val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    fun init() {
        mediaPlayer = MediaPlayer.create(context, soundFile)
    }

    fun play() {
        mediaPlayer?.start()
    }


    fun release() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }
}