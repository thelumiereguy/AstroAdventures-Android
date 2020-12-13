package com.thelumierguy.astroadventures.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

object PlayerHealthInfo {

    const val MAX_HEALTH = 20

    private val playerHealth = MutableStateFlow(MAX_HEALTH)

    fun getPlayerHealthFlow(): Flow<Int> = playerHealth

    fun getPlayerHealthValue() = playerHealth.value

    fun onHit() {
        Log.d("Health", "${playerHealth.value}")
        playerHealth.value -= 2
    }

    fun resetHealth() {
        playerHealth.value = 20
    }
}