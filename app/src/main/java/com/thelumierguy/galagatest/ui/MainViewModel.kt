package com.thelumierguy.galagatest.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {

    private val screenStateFlow: MutableStateFlow<ScreenStates> =
        MutableStateFlow(ScreenStates.AppInit)

    fun observeScreenState(): StateFlow<ScreenStates> = screenStateFlow

    fun updateUIState(screenStates: ScreenStates) {
        screenStateFlow.value = screenStates
    }
}

sealed class ScreenStates {
    object AppInit : ScreenStates()
    object GameMenu : ScreenStates()
    object StartGame : ScreenStates()
    object LevelComplete : ScreenStates()
    object GameOver : ScreenStates()
}