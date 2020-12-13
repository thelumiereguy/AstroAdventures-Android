package com.thelumierguy.astroadventures.ui.game.views.levelzero

import android.util.TypedValue
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionManager
import com.thelumierguy.astroadventures.data.*
import com.thelumierguy.astroadventures.data.DataStoreHelper.setHasCompletedTutorial
import com.thelumierguy.astroadventures.databinding.LevelZeroGameBinding
import com.thelumierguy.astroadventures.ui.MainActivity
import com.thelumierguy.astroadventures.ui.ScreenStates
import com.thelumierguy.astroadventures.ui.game.views.bullets.BulletView
import com.thelumierguy.astroadventures.ui.game.views.bullets.LevelZeroCallBackBullet
import com.thelumierguy.astroadventures.ui.game.views.enemyShip.EnemyDetailsCallback
import com.thelumierguy.astroadventures.ui.game.views.enemyShip.OnCollisionCallBack
import com.thelumierguy.astroadventures.ui.game.views.playership.LevelZeroCallBackPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

interface LevelZeroHelper {

    fun MainActivity.showInitialInstructions(
        levelZeroGameBinding: LevelZeroGameBinding,
        bulletStore: BulletStore,
    ) {
        levelZeroGameBinding.apply {
            lifecycleScope.launch {
                dialogView.addDialog("Greetings Ranger!. It's good to have you back after so long. As you know, our planet's resources are all but depleted.")
                delay(10000)
                dialogView.addDialog("It is our job to go on this mission to help our world. I assume you remember the workings around here. But let's go over it again, shall we?")
                delay(10000)
                dialogView.addDialog("Please tilt your phone to control your ship.")
                delay(3000)
                spaceShipView.startGame()
                spaceShipView.levelZeroCallBackPlayer = object : LevelZeroCallBackPlayer {
                    override fun onTilted() {
                        lifecycleScope.launch {
                            dialogView.addDialog("Good Job. Try conserving your resources as they are hard to come by these days.")
                            delay(10000)
                            dialogView.addDialog("At the top-left, you'll find information regarding your Ammunition reserves.")
                            ammoCountView.isVisible = true
                            delay(8000)
                            dialogView.addDialog("And at the bottom, you'll find information regarding your Spaceship's health.")
                            healthView.isVisible = true
                            delay(8000)
                            dialogView.addDialog("Now tap on your screen to fire a missile.")
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
                            bulletView.isVisible = true
                        }
                    }
                }
                bulletView.levelZeroCallBackBullet = object : LevelZeroCallBackBullet {
                    override fun onFired() {
                        lifecycleScope.launch {
                            dialogView.addDialog("Good Job!")
                            delay(5000)
                            dialogView.addDialog("Hold on! There's an incoming transmission.")
                            delay(8000)
                            dialogView.addDialog("Mayday! Mayday! Someone just fired a missile and our location has been compromised. " +
                                    "Now, all of China knows we're here.")
                            delay(15000)
                            dialogView.addDialog("There's a fleet of Space Pirates heading your way. Finish them off as soon as possible.")
                            dropsView.isVisible = true
                            delay(10000)
                            enemiesView.animate()
                                .translationYBy(TypedValue.applyDimension(
                                    TypedValue.COMPLEX_UNIT_DIP,
                                    100F,
                                    resources.displayMetrics
                                ))
                                .setDuration(1000L)
                                .withEndAction {
                                    lifecycleScope.launch {
                                        dialogView.addDialog("You'll occasionally see an enemy drop a Blue Capsule. Be sure to collect it to gather more ammunition.")
                                        delay(10000)
                                        dialogView.addDialog("Good Luck!")
                                        initUIComponents(levelZeroGameBinding,
                                            this@showInitialInstructions)
                                        delay(4000)
                                        scoreView.isVisible = true
                                        LevelInfo.hasPlayedTutorial = true
                                        setHasCompletedTutorial()
                                        TransitionManager.beginDelayedTransition(root)
                                        dialogView.isVisible = false
                                        enemiesView.startGame()
                                    }
                                }.start()

                        }
                    }
                }
            }
        }
    }

    fun MainActivity.initUIComponents(
        levelZeroGameBinding: LevelZeroGameBinding,
        mainActivity: MainActivity,
    ) {
        levelZeroGameBinding.guideline.updateLayoutParams<ConstraintLayout.LayoutParams> {
            guidePercent = 0.88F
        }
        levelZeroGameBinding.healthView.onHealthEmpty = {
            mainActivity.viewModel.updateUIState(ScreenStates.YouDied)
        }
        val softBodyObjectTracker = object : SoftBodyObject.SoftBodyObjectTracker {
            override fun initBulletTracking(softBodyObjectData: SoftBodyObjectData) {
                if (softBodyObjectData.sender == BulletView.Sender.PLAYER) {
                    levelZeroGameBinding.enemiesView.checkCollision(softBodyObjectData)
                } else {
                    levelZeroGameBinding.spaceShipView.checkCollision(softBodyObjectData)
                }
            }

            override fun cancelTracking(bulletId: UUID, sender: BulletView.Sender) {
                if (sender == BulletView.Sender.PLAYER) {
                    levelZeroGameBinding.spaceShipView.removeSoftBodyEntry(bulletId)
                } else {
                    levelZeroGameBinding.enemiesView.removeSoftBodyEntry(bulletId)
                }
            }

        }
        val onCollisionCallBack = object : OnCollisionCallBack {
            override fun onCollision(softBodyObject: SoftBodyObjectData) {
                if (softBodyObject.objectType == SoftBodyObjectType.BULLET) {
                    levelZeroGameBinding.bulletView.destroyBullet(softBodyObject.objectId)
                } else {
                    levelZeroGameBinding.dropsView.destroyObject(softBodyObject.objectId)
                }
            }

        }

        val enemyDetailsCallback = object : EnemyDetailsCallback {
            override fun onAllEliminated(ammoCount: Int) {
                lifecycleScope.launchWhenCreated {
                    showLevelCompleteScene(ammoCount)
                }
            }

            override fun onCanonReady(enemyX: Float, enemyY: Float) {
                levelZeroGameBinding.bulletView.fire(enemyX, enemyY, BulletView.Sender.ENEMY)
            }

            override fun hasDrop(enemyX: Float, enemyY: Float) {
                levelZeroGameBinding.dropsView.dropGift(enemyX, enemyY)
            }

            override fun onGameOver() {
                viewModel.updateUIState(ScreenStates.YouDied)
            }
        }
        levelZeroGameBinding.apply {
            bulletView.softBodyObjectTracker = softBodyObjectTracker
            dropsView.softBodyObjectTracker = softBodyObjectTracker
            enemiesView.enemyDetailsCallback = enemyDetailsCallback
            enemiesView.onCollisionCallBack = onCollisionCallBack
            spaceShipView.onCollisionCallBack = onCollisionCallBack
        }
    }
}