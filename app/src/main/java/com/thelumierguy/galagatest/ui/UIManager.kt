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
import com.thelumierguy.galagatest.data.BulletStore
import com.thelumierguy.galagatest.data.Score.resetScore
import com.thelumierguy.galagatest.data.Score.scoreFlow
import com.thelumierguy.galagatest.utils.addTransition
import com.thelumierguy.galagatest.utils.transitionSet
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect


fun MainActivity.observeScreenStates() {
    lifecycleScope.launchWhenCreated {
        viewModel.observeScreenState()
            .collect {
                when (it) {
                    ScreenStates.AppInit -> {
                        transitionTo(initScene.scene, Fade(Fade.MODE_IN))
                    }

                    ScreenStates.GameMenu -> {
                        gameMenuScene.binding.apply {

                            val transition = transitionSet {

                                ordering = ORDERING_SEQUENTIAL

                                addTransition(Slide(Gravity.TOP)) {
                                    addTarget(logoView)
                                    duration = 300L
                                }

                                addTransition(Slide(Gravity.BOTTOM)) {
                                    addTarget(btnStart)
                                    duration = 300L
                                }

                                addTransition(Slide(Gravity.BOTTOM)) {
                                    addTarget(btnViewScores)
                                    duration = 300L
                                }

                                addTransition(Slide(Gravity.BOTTOM)) {
                                    addTarget(btnExit)
                                    duration = 300L
                                }

                                interpolator = AccelerateDecelerateInterpolator()
                            }
                            transitionFromTo(initScene.scene, gameMenuScene.scene, transition)
                        }
                        gameMenuScene.binding.btnStart.setOnClickListener {
                            viewModel.updateUIState(ScreenStates.StartGame)
                        }
                        gameMenuScene.binding.btnViewScores.setOnClickListener {

                        }
                        gameMenuScene.binding.btnExit.setOnClickListener {
                            finish()
                        }
                    }

                    ScreenStates.StartGame -> {
                        resetScore()
                        gameScene.binding.apply {
                            val bulletStore = BulletStore(BulletStore.HALF_REFILL) {
                                bulletView.isEnabled = false
                                viewModel.updateUIState(ScreenStates.RanOutOfAmmo)
                            }
                            bulletView.bulletTracker = this@observeScreenStates
                            enemiesView.enemyDetailsCallback = this@observeScreenStates
                            enemiesView.onCollisionDetector = this@observeScreenStates
                            bulletView.setOnClickListener {
                                bulletStore.updateInventory()
                                bulletView.shipY = spaceShipView.getShipY()
                                bulletView.shipX = spaceShipView.getShipX()
                            }
                            lifecycleScope.launchWhenCreated {
                                scoreFlow().collect { score ->
                                    scoreView.text =
                                        getString(R.string.score_text, score)
                                }
                            }
                            lifecycleScope.launchWhenCreated {
                                bulletStore.bulletCountFlow().collect { ammoCount ->
                                    ammoCountView.setBulletCount(ammoCount, bulletStore.maxCount)
                                }
                            }
                        }
                        transitionFromTo(gameMenuScene.scene, gameScene.scene, Fade(Fade.MODE_IN))
                    }


                    ScreenStates.LevelComplete -> {
                        levelCompleteScene.binding.apply {
                            scoreView.text =
                                getString(R.string.score_text, scoreFlow().value)
                        }

                        transitionFromTo(gameScene.scene,
                            levelCompleteScene.scene,
                            Fade(Fade.MODE_IN).apply {
                                duration = 600L
                                interpolator = AccelerateDecelerateInterpolator()
                            })
                    }


                    ScreenStates.GameOver -> {
                        gameOverScene.binding.apply {
                            scoreView.text =
                                getString(R.string.score_text, scoreFlow().value)

                            btnMainMenu.setOnClickListener {
                                viewModel.updateUIState(ScreenStates.GameMenu)
                            }


                            btnTryAgain.setOnClickListener {
                                viewModel.updateUIState(ScreenStates.StartGame)
                            }
                        }

                        transitionFromTo(youDiedScene.scene,
                            gameOverScene.scene,
                            Fade(Fade.MODE_IN).apply {
                                duration = 600L
                                interpolator = AccelerateDecelerateInterpolator()
                            })
                    }


                    ScreenStates.YouDied -> {
                        transitionFromTo(gameScene.scene,
                            youDiedScene.scene,
                            Fade(Fade.MODE_IN).apply {
                                addTarget(youDiedScene.binding.diedText)
                                duration = 1600L
                            })
                        lifecycleScope.launchWhenCreated {
                            delay(2000L)
                            viewModel.updateUIState(ScreenStates.GameOver)
                        }
                    }


                    ScreenStates.RanOutOfAmmo -> {
                        youDiedScene.binding.diedText.text = getString(R.string.you_lost)
                        transitionFromTo(gameScene.scene,
                            youDiedScene.scene,
                            Fade(Fade.MODE_IN).apply {
                                addTarget(youDiedScene.binding.diedText)
                                duration = 1600L
                            })
                        lifecycleScope.launchWhenCreated {
                            delay(2000L)
                            viewModel.updateUIState(ScreenStates.GameOver)
                        }
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