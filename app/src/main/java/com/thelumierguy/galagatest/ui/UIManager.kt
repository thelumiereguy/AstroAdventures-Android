package com.thelumierguy.galagatest.ui

import androidx.lifecycle.lifecycleScope
import androidx.transition.Fade
import androidx.transition.Scene
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect


fun MainActivity.observeScreenStates() {
    lifecycleScope.launchWhenCreated {
        viewModel.observeScreenState().collect {
            when (it) {
                ScreenStates.AppInit -> {
                    transitionTo(initScene.scene, Fade(Fade.MODE_IN))
                }
                ScreenStates.GameMenu -> {
                    transitionFromTo(initScene.scene, gameMenuScene.scene, Fade(Fade.MODE_IN))
                }
                ScreenStates.StartGame -> {
                    transitionFromTo(gameMenuScene.scene, startGameScene.scene, Fade(Fade.MODE_IN))
                    startGameScene.binding.bulletView.bulletTracker = this@observeScreenStates
                    startGameScene.binding.enemiesView.onCollisionDetector =
                        this@observeScreenStates
                    startGameScene.binding.bulletView.setOnClickListener {
                        startGameScene.binding.bulletView.shipY =
                            startGameScene.binding.spaceShipView.getShipY()
                        startGameScene.binding.bulletView.shipX =
                            startGameScene.binding.spaceShipView.getShipX()
                    }
                }
                ScreenStates.LevelComplete -> {

                }
                ScreenStates.GameOver -> {

                }
            }
        }
    }

    lifecycleScope.launchWhenCreated {
        delay(2000)
        viewModel.updateUIState(ScreenStates.GameMenu)
    }
}

data class SceneContainer<Binding : ViewBinding>(val binding: Binding, val scene: Scene)