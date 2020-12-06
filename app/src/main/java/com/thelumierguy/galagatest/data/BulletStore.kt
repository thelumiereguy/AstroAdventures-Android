package com.thelumierguy.galagatest.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BulletStore {

    private val bulletCountFlow = MutableStateFlow(0L)

    fun scoreFlow(): StateFlow<Long> = bulletCountFlow

    fun updateScore(score: Long) {
        bulletCountFlow.value += score
    }

    fun resetScore() {
        bulletCountFlow.value = 0L
    }

    fun saveScore() {

    }

}