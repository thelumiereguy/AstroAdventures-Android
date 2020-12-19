package com.thelumierguy.astroadventures.ui.game.views.levelzero

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionManager
import com.thelumierguy.astroadventures.R
import com.thelumierguy.astroadventures.data.*
import com.thelumierguy.astroadventures.data.DataStoreHelper.setHasCompletedTutorial
import com.thelumierguy.astroadventures.databinding.LevelZeroGameBinding
import com.thelumierguy.astroadventures.ui.MainActivity
import com.thelumierguy.astroadventures.ui.ScreenStates
import com.thelumierguy.astroadventures.ui.game.views.bullets.BulletView
import com.thelumierguy.astroadventures.ui.game.views.bullets.LevelZeroCallBackBullet
import com.thelumierguy.astroadventures.ui.game.views.enemyShip.EnemyDetailsCallback
import com.thelumierguy.astroadventures.ui.game.views.enemyShip.OnCollisionCallBack
import com.thelumierguy.astroadventures.ui.game.views.instructions.DialogHelper
import com.thelumierguy.astroadventures.ui.game.views.playership.LevelZeroCallBackPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

interface LevelZeroHelper {

    fun MainActivity.showInitialInstructions(
        levelZeroGameBinding: LevelZeroGameBinding,
        bulletStore: BulletStore,
    ) {
        var dialogJob: Job = Job()

        levelZeroGameBinding.apply {
            val dialogHelper = DialogHelper()

            suspend fun LevelZeroGameBinding.handleDialogs() {
                val nextDialog = dialogHelper.getNextDialog()
                nextDialog?.let { dialog ->
                    when (dialog.type) {
                        DialogHelper.InstructionType.TEXT -> {
                            dialogView.alpha = 1F
                            tvBlinking.stopBlinking()
                            dialogView.addDialog(dialog.text)
                            delay(dialog.duration)
                        }
                        DialogHelper.InstructionType.AMMO -> {
                            dialogView.addDialog(dialog.text)
                            ammoCountView.isVisible = true
                            delay(dialog.duration)
                        }
                        DialogHelper.InstructionType.HEALTH -> {
                            dialogView.addDialog(dialog.text)
                            healthView.isVisible = true
                            delay(dialog.duration)
                        }
                        DialogHelper.InstructionType.AmmoWarning -> {
                            dialogHelper.setLock()
                            dialogView.addDialog(dialog.text)
                            delay(dialog.duration)
                            dialogHelper.removeLock()
                        }
                        DialogHelper.InstructionType.TILT -> {
                            dialogHelper.setLock()
                            dialogView.isInvisible = true
                            spaceShipView.levelZeroCallBackPlayer =
                                object : LevelZeroCallBackPlayer {
                                    override fun onTilted() {
                                        dialogHelper.removeLock()
                                        root.post {
                                            dialogView.performClick()
                                        }
                                    }
                                }
                            spaceShipView.startGame()
                            tvBlinking.text = dialog.text
                            tvBlinking.startBlinking()
                        }
                        DialogHelper.InstructionType.FIRE -> {
                            dialogHelper.setLock()
                            dialogView.isInvisible = true
                            tvBlinking.text = dialog.text
                            bulletView.levelZeroCallBackBullet = object : LevelZeroCallBackBullet {
                                override fun onFired() {
                                    dialogHelper.removeLock()
                                    root.post {
                                        dialogView.performClick()
                                    }
                                }
                            }
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
                            tvBlinking.startBlinking()
                        }
                        DialogHelper.InstructionType.EnemySpotted -> {
                            dialogHelper.setLock()
                            enemiesView.disableInit = true
                            enemiesView.animate()
                                .translationYBy((resources.getDimension(R.dimen.enemyTranslateY)))
                                .setDuration(2000L)
                                .withEndAction {
                                    dialogHelper.removeLock()
                                    dialogView.performClick()
                                }.start()
                        }
                        DialogHelper.InstructionType.EnemyTranslated -> {
                            dialogHelper.setLock()
                            dialogView.addDialog(dialog.text)
                            initUIComponents(levelZeroGameBinding,
                                this@showInitialInstructions)
                            scoreView.isVisible = true
                            LevelInfo.hasPlayedTutorial = true
                            setHasCompletedTutorial()
                            TransitionManager.beginDelayedTransition(root)
                            delay(dialog.duration)
                            dialogView.isVisible = false
                            enemiesView.startGame()
                            return
                        }
                    }
                }
            }

            fun startLoopingThroughDialogs() {
                dialogJob.cancel()
                dialogJob = lifecycleScope.launch {
                    (dialogHelper.counter..dialogHelper.getItemSize()).forEach { _ ->
                        handleDialogs()
                    }
                }
            }

            dialogView.setOnClickListener {
                if (dialogHelper.isOpen)
                    startLoopingThroughDialogs()
            }

            startLoopingThroughDialogs()
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
                showLevelCompleteScene(ammoCount)
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