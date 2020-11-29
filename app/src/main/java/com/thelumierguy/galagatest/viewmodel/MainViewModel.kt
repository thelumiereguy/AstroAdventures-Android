package com.thelumierguy.galagatest.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {

    private val screenStateFlow: MutableStateFlow<ScreenStates> =
        MutableStateFlow(ScreenStates.APP_INIT)

    fun observeScreenState(): StateFlow<ScreenStates> = screenStateFlow

    fun updateUIState(screenStates: ScreenStates) {
        screenStateFlow.value = screenStates
    }
}

sealed class ScreenStates {
    object APP_INIT : ScreenStates()
    object GAME_MENU : ScreenStates()
    object START_GAME : ScreenStates()
}