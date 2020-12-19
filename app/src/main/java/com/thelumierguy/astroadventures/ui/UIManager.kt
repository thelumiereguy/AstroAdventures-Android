package com.thelumierguy.astroadventures.ui

import android.media.MediaPlayer
import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.lifecycleScope
import androidx.transition.Fade
import androidx.transition.Scene
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet.ORDERING_SEQUENTIAL
import androidx.viewbinding.ViewBinding
import com.thelumierguy.astroadventures.R
import com.thelumierguy.astroadventures.data.*
import com.thelumierguy.astroadventures.data.BulletStore.Companion.getAmountScore
import com.thelumierguy.astroadventures.data.Score.saveScore
import com.thelumierguy.astroadventures.data.Score.scoreFlow
import com.thelumierguy.astroadventures.databinding.GameSceneBinding
import com.thelumierguy.astroadventures.ui.game.views.bullets.BulletView
import com.thelumierguy.astroadventures.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


fun MainActivity.observeScreenStates() {
    lifecycleScope.launchWhenCreated {
        viewModel.observeScreenState()
            .collect {
                uiEventJob.cancel()
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
                            if (LevelInfo.hasPlayedTutorial) {
                                LevelInfo.resetLevel()
                            }
                            viewModel.updateUIState(ScreenStates.LevelStart)
                        }
                        gameMenuScene.binding.btnViewScores.setOnClickListener {
                            viewModel.updateUIState(ScreenStates.ViewHighScores)
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
                                uiEventJob = lifecycleScope.launchWhenCreated {
                                    if (isActive) {
                                        (3 downTo 1).forEach {
                                            levelStartScene.binding.timerView.text = it.toString()
                                            delay(1000)
                                        }
                                        if (LevelInfo.hasPlayedTutorial)
                                            viewModel.updateUIState(ScreenStates.StartGame)
                                        else
                                            viewModel.updateUIState(ScreenStates.StartLevelZero)
                                    }
                                }
                            }
                        })
                    }
                    is ScreenStates.StartGame -> {
                        resetGameScene()
                        gameScene.binding.apply {
                            val bulletStore = BulletStore(BulletStore.HALF_REFILL)
                            enemiesView.bulletStore = bulletStore
                            spaceShipView.bulletStore = bulletStore
                            spaceShipView.onHitCallBack = {
                                containerView.startShakeAnimation(50, 15, 4)
                            }
                            lifecycleScope.launchWhenCreated {
                                scoreFlow().collect { score ->
                                    if (isActive) {
                                        scoreView.text =
                                            getString(R.string.score_text, score)
                                    }
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
                                                BulletView.Sender.PLAYER)
                                        } else {
                                            viewModel.updateUIState(ScreenStates.RanOutOfAmmo)
                                        }
                                    }
                                }
                            })
                        }
                    }

                    is ScreenStates.StartLevelZero -> {
                        startLevelZero()
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

                            uiEventJob = lifecycleScope.launchWhenCreated {
                                delay(1500L)
                                val constraint2 = ConstraintSet()
                                constraint2.clone(root.context,
                                    R.layout.level_complete_scoreboard_scene)

                                TransitionManager.beginDelayedTransition(root)
                                constraint2.applyTo(root)


                                lifecycleScope.launchWhenCreated {
                                    delay(800L)
                                    bulletCountValueView.addNewValue(getAmountScore(it.bulletCount).toFloat()) {
                                        val newScore =
                                            scoreFlow().value.toFloat() + getAmountScore(it.bulletCount)
                                        Score.updateScore(getAmountScore(it.bulletCount).toLong())
                                        totalScoreValueView.addNewValue(newScore)
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
                            saveScore(lifecycleScope)
                        }
                    }
                    ScreenStates.ViewHighScores -> {
                        transitionTo(highScoreScene.scene, Fade(Fade.MODE_IN))
                        highScoreScene.binding.apply {
                            lifecycleScope.launch {
                                DataStoreHelper.getHighScore()
                                    .zip(DataStoreHelper.getMaxLevels()) { highScore, maxLevels ->
                                        Pair(maxLevels, highScore)
                                    }.collect {
                                        scoreBoard.text =
                                            getString(R.string.level, it.first, it.second)
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
                        saveScore(lifecycleScope)
                        transitionFromTo(youDiedScene.scene,
                            gameOverScene.scene,
                            Fade(Fade.MODE_IN).apply {
                                duration = 600L
                                interpolator = AccelerateDecelerateInterpolator()
                            })
                    }


                    ScreenStates.YouDied -> {
                        val deathSoundManager = MediaPlayer.create(
                            this@observeScreenStates, R.raw.player_explosion
                        ).also {
                            it.setOnPreparedListener {
                                it.start()
                            }
                        }
                        delay(500)
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
                                    deathSoundManager.release()
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

private fun MainActivity.startLevelZero() {
    resetGameScene()
    levelZeroGameScene.binding.apply {
        val bulletStore = BulletStore(BulletStore.FULL_REFILL)

        enemiesView.bulletStore = bulletStore
        spaceShipView.bulletStore = bulletStore

        lifecycleScope.launch {
            scoreFlow().collect { score ->
                scoreView.text =
                    getString(R.string.score_text, score)
            }
        }

        lifecycleScope.launch {
            bulletStore.bulletCountFlow().collect { ammoCount ->
                ammoCountView.setBulletCount(ammoCount, bulletStore.maxCount)
            }
        }

        transitionFromTo(levelStartScene.scene,
            levelZeroGameScene.scene,
            transitionSet {
                addTransition(Slide(Gravity.BOTTOM)) {
                    duration = 1200
                    interpolator = AccelerateDecelerateInterpolator()
                    addTarget(spaceShipView)
                }
                onEnd {
                    showInitialInstructions(this@apply, bulletStore)
                }
            })
    }
}

fun startGame(binding: GameSceneBinding) {
    binding.spaceShipView.startGame()
    binding.enemiesView.startGame()
}

data class SceneContainer<Binding : ViewBinding>(val binding: Binding, val scene: Scene)