package com.thelumierguy.astroadventures.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_MUSIC
import android.media.SoundPool

class SoundManager(private val soundFile: Int, val context: Context) {

    private var soundPool: SoundPool? = null

    var soundId: Int? = null

    fun init() {
        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(AudioAttributes.Builder().setContentType(CONTENT_TYPE_MUSIC)
                .build())
            .build()
        soundId = soundPool?.load(context, soundFile, 1)
    }

    fun play() {
        soundId?.let { soundPool?.play(it, 1F, 1F, 1, 0, 1f) }
    }


    fun release() {
        soundPool?.release()
        soundPool = null
    }
}