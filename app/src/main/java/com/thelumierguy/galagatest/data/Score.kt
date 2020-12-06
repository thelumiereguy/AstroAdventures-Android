package com.thelumierguy.galagatest.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object Score {
    private val scoreFlow = MutableStateFlow(0L)

    fun scoreFlow(): StateFlow<Long> = scoreFlow

    fun updateScore(score: Long) {
        scoreFlow.value += score
    }

    fun resetScore() {
        scoreFlow.value = 0L
    }

    fun saveScore() {

    }

}