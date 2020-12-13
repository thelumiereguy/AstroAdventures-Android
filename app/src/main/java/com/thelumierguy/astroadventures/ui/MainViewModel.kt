package com.thelumierguy.astroadventures.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {

    var previousState: ScreenStates = ScreenStates.AppInit

    private val screenStateFlow: MutableStateFlow<ScreenStates> =
        MutableStateFlow(ScreenStates.AppInit)

    fun observeScreenState(): StateFlow<ScreenStates> = screenStateFlow

    fun updateUIState(screenStates: ScreenStates) {
        if (screenStateFlow.value != screenStates) {
            previousState = screenStateFlow.value
            screenStateFlow.value = screenStates
        }
    }
}

sealed class ScreenStates {
    object AppInit : ScreenStates()
    object GameMenu : ScreenStates()
    object LevelStart : ScreenStates()
    object StartLevelZero : ScreenStates()
    object StartGame : ScreenStates()
    object ViewHighScores : ScreenStates()
    object LevelStartWarp : ScreenStates()
    data class LevelComplete(val bulletCount: Int) : ScreenStates()
    object YouDied : ScreenStates()
    object RanOutOfAmmo : ScreenStates()
    object GameOver : ScreenStates()
}