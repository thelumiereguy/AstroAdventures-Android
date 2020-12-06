package com.thelumierguy.galagatest.ui

import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.lifecycle.lifecycleScope
import androidx.transition.Fade
import androidx.transition.Scene
import androidx.transition.Slide
import androidx.transition.TransitionSet.ORDERING_SEQUENTIAL
import androidx.viewbinding.ViewBinding
import com.thelumierguy.galagatest.R
import com.thelumierguy.galagatest.data.Score.resetScore
import com.thelumierguy.galagatest.data.Score.scoreFlow
import com.thelumierguy.galagatest.utils.transitionSet
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
                    gameMenuScene.binding.apply {

                        val transition = transitionSet {

                            ordering = ORDERING_SEQUENTIAL

                            addTransition(Slide(Gravity.TOP).apply {
                                addTarget(logoView)
                                duration = 300L
                            })

                            addTransition(Slide(Gravity.BOTTOM).apply {
                                addTarget(btnStart)
                                duration = 300L
                            })

                            addTransition(Slide(Gravity.BOTTOM).apply {
                                addTarget(btnViewScores)
                                duration = 300L
                            })

                            addTransition(Slide(Gravity.BOTTOM).apply {
                                addTarget(btnExit)
                                duration = 300L
                            })

                            interpolator = AccelerateDecelerateInterpolator()
                        }
                        transitionFromTo(initScene.scene, gameMenuScene.scene, transition)
                    }
                    gameMenuScene.binding.btnStart.setOnClickListener {
                        resetScore()
                        viewModel.updateUIState(ScreenStates.StartGame)
                    }
                    gameMenuScene.binding.btnViewScores.setOnClickListener {

                    }
                    gameMenuScene.binding.btnExit.setOnClickListener {
                        finish()
                    }
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
                    scoreFlow().collect { score ->
                        startGameScene.binding.scoreView.text =
                            getString(R.string.score_text, score)
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