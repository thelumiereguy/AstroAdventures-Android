package com.thelumierguy.galagatest.ui

import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.lifecycleScope
import androidx.transition.*
import androidx.transition.TransitionSet.ORDERING_SEQUENTIAL
import androidx.viewbinding.ViewBinding
import com.thelumierguy.galagatest.R
import com.thelumierguy.galagatest.data.BulletStore
import com.thelumierguy.galagatest.data.BulletStore.Companion.getAmountScore
import com.thelumierguy.galagatest.data.LevelInfo
import com.thelumierguy.galagatest.data.PlayerHealthInfo
import com.thelumierguy.galagatest.data.Score
import com.thelumierguy.galagatest.data.Score.scoreFlow
import com.thelumierguy.galagatest.databinding.GameSceneBinding
import com.thelumierguy.galagatest.ui.game.views.bullets.BulletView
import com.thelumierguy.galagatest.utils.addTransition
import com.thelumierguy.galagatest.utils.onEnd
import com.thelumierguy.galagatest.utils.transitionSet
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect


fun MainActivity.observeScreenStates() {
    lifecycleScope.launchWhenCreated {
        viewModel.observeScreenState()
            .collect {
                when (it) {
                    ScreenStates.AppInit -> {
                        backgroundMusicManager.startPlaying()
                        transitionTo(initScene.scene, Fade(Fade.MODE_IN))
                    }

                    ScreenStates.GameMenu -> {
                        backgroundMusicManager.startPlaying()
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
                            if (viewModel.previousState == ScreenStates.GameOver) {
                                transitionFromTo(gameOverScene.scene,
                                    gameMenuScene.scene,
                                    transition)
                            } else {
                                transitionFromTo(initScene.scene, gameMenuScene.scene, transition)
                            }
                        }
                        gameMenuScene.binding.btnStart.setOnClickListener {
                            backgroundMusicManager.stopPlaying()
                            LevelInfo.resetLevel()
                            viewModel.updateUIState(ScreenStates.LevelStart)
                        }
                        gameMenuScene.binding.btnViewScores.setOnClickListener {

                        }
                        gameMenuScene.binding.btnExit.setOnClickListener {
                            finish()
                        }
                    }
                    ScreenStates.LevelStart -> {
                        levelStartScene.binding.timerView.text = ""
                        levelStartScene.binding.levelStartText.text =
                            getString(R.string.level_start, LevelInfo.level)
                        if (LevelInfo.level == 1) {
                            Score.resetScore()
                            PlayerHealthInfo.resetHealth()
                        }
                        transitionTo(levelStartScene.scene, Fade(Fade.MODE_IN).apply {
                            onEnd {
                                lifecycleScope.launchWhenCreated {
                                    (3 downTo 1).forEach {
                                        levelStartScene.binding.timerView.text = it.toString()
                                        delay(1000)
                                    }
                                    viewModel.updateUIState(ScreenStates.StartGame)
                                }
                            }
                        })
                    }
                    is ScreenStates.StartGame -> {
                        startGameScene()
                        gameScene.binding.apply {
                            val bulletStore = BulletStore(BulletStore.HALF_REFILL)
                            bulletView.bulletTracker = this@observeScreenStates
                            healthView.onHealthEmpty = {
                                viewModel.updateUIState(ScreenStates.YouDied)
                            }
                            enemiesView.enemyDetailsCallback = this@observeScreenStates
                            enemiesView.onCollisionDetector = this@observeScreenStates
                            spaceShipView.onCollisionDetector = this@observeScreenStates
                            enemiesView.bulletStore = bulletStore

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

                            transitionFromTo(levelStartScene.scene, gameScene.scene, transitionSet {
                                addTransition(Slide(Gravity.BOTTOM)) {
                                    duration = 1200
                                    interpolator = AccelerateDecelerateInterpolator()
                                    addTarget(spaceShipView)
                                }
                                addTransition(Slide(Gravity.TOP)) {
                                    duration = 2200
                                    interpolator = LinearInterpolator()
                                    addTarget(enemiesView)
                                }
                                onEnd {
                                    startGame(gameScene.binding)
                                    bulletView.setOnClickListener {
                                        if (bulletStore.getAmmoCount() != 0) {
                                            bulletStore.updateInventory()
                                            bulletView.fire(spaceShipView.getShipX(),
                                                spaceShipView.getShipY(),
                                                BulletView.BulletSender.PLAYER)
                                        } else {
                                            viewModel.updateUIState(ScreenStates.RanOutOfAmmo)
                                        }
                                    }
                                }
                            })
                        }
                    }


                    is ScreenStates.LevelComplete -> {
                        transitionFromTo(gameScene.scene,
                            levelCompleteScene.scene,
                            Fade(Fade.MODE_IN).apply {
                                duration = 700
                            })

                        levelCompleteScene.binding.apply {
                            successText.text = getString(R.string.level_complete, LevelInfo.level)
                            scoreViewValue.text = scoreFlow().value.toString()

                            lifecycleScope.launchWhenCreated {
                                delay(1500L)
                                val constraint2 = ConstraintSet()
                                constraint2.clone(root.context,
                                    R.layout.level_complete_scoreboard_scene)

                                TransitionManager.beginDelayedTransition(root)
                                constraint2.applyTo(root)


                                lifecycleScope.launchWhenCreated {
                                    delay(800L)
                                    bulletCountValueView.addNewValue(getAmountScore(it.bulletCount).toFloat()) {
                                        totalScoreValueView.addNewValue(scoreFlow().value.toFloat() + getAmountScore(
                                            it.bulletCount))
                                    }
                                }

                                btnMainMenu.setOnClickListener {
                                    viewModel.updateUIState(ScreenStates.GameMenu)
                                }


                                btnContinue.setOnClickListener {
                                    LevelInfo.increment()
                                    viewModel.updateUIState(ScreenStates.LevelStartWarp)
                                }
                            }
                        }
                    }

                    ScreenStates.LevelStartWarp -> {
                        transitionFromTo(levelCompleteScene.scene,
                            levelStartWarpScene.scene,
                            Fade(Fade.MODE_IN).apply {
                                duration = 200
                            })
                        binding.root.setTrails(true)
                        levelStartWarpScene.binding.spaceShipView.animate()
                            .translationY(-binding.root.measuredHeight.toFloat())
                            .withEndAction {
                                binding.root.setTrails(false)
                                viewModel.updateUIState(ScreenStates.LevelStart)
                            }
                            .setDuration(4000L)
                            .start()
                    }
                    ScreenStates.GameOver -> {
                        gameOverScene.binding.apply {
                            scoreView.text =
                                getString(R.string.score_text, scoreFlow().value)

                            if (viewModel.previousState == ScreenStates.YouDied) {
                                diedSubtitle.text = getString(R.string.enemyBreached)
                            } else {
                                val conserveAmmoText = getString(R.string.conserveAmmo)
                                diedSubtitle.text =
                                    conserveAmmoText + getString(R.string.enemyBreached)
                            }

                            btnMainMenu.setOnClickListener {
                                viewModel.updateUIState(ScreenStates.GameMenu)
                            }


                            btnTryAgain.setOnClickListener {
                                LevelInfo.resetLevel()
                                viewModel.updateUIState(ScreenStates.LevelStart)
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
                        youDiedScene.binding.diedText.animate().alpha(1F).scaleX(1.5F).scaleY(1.5F)
                            .setDuration(2200).withEndAction {
                                lifecycleScope.launchWhenCreated {
                                    delay(2000L)
                                    viewModel.updateUIState(ScreenStates.GameOver)
                                }
                            }.start()
                    }
                    ScreenStates.RanOutOfAmmo -> {
                        viewModel.updateUIState(ScreenStates.GameOver)
                    }
                }
            }
    }

    lifecycleScope.launchWhenCreated {
        delay(2000)
        viewModel.updateUIState(ScreenStates.GameMenu)
    }
}

fun startGame(binding: GameSceneBinding) {
    binding.spaceShipView.startGame()
    binding.enemiesView.startGame()
}

data class SceneContainer<Binding : ViewBinding>(val binding: Binding, val scene: Scene)