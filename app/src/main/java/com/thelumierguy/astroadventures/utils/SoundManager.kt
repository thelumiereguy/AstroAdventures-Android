package com.thelumierguy.astroadventures.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_MUSIC
import android.media.SoundPool

class SoundManager(val context: Context, private val soundData: SoundData) {

    private var soundPool: SoundPool? = null

    private val soundMap: MutableMap<String, Int?> = mutableMapOf()

    fun init() {
        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(AudioAttributes.Builder().setContentType(CONTENT_TYPE_MUSIC)
                .build())
            .build()
        soundMap[soundData.soundName] = soundPool?.load(context, soundData.soundFile, 1)

    }

    fun play(soundName: String) {
        val soundId = soundMap[soundName]
        soundId?.let { soundPool?.play(it, 1F, 1F, 1, 0, 1f) }
    }


    fun release() {
        soundMap.clear()
        soundPool?.release()
        soundPool = null
    }
}

data class SoundData(val soundFile: Int, val soundName: String)

const val PLAYER_EXPLOSION_SOUND = "PLAYER_EXPLOSION_SOUND"
const val PLAYER_BULLET_SOUND = "PLAYER_BULLET_SOUND"