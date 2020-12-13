package com.thelumierguy.astroadventures.data

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

object Score {
    private val scoreFlow = MutableStateFlow(0L)

    fun scoreFlow(): StateFlow<Long> = scoreFlow

    fun updateScore(score: Long) {
        scoreFlow.value += score
    }

    fun resetScore() {
        scoreFlow.value = 0L
    }

    fun saveScore(lifCycleScope: LifecycleCoroutineScope) {
        lifCycleScope.launch {
            DataStoreHelper.getHighScore().collect {
                if (it < scoreFlow.value) {
                    DataStoreHelper.setHighScore(scoreFlow.value)
                }
            }
        }
    }

}