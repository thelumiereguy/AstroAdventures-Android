package com.thelumierguy.galagatest.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.transition.Scene
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.thelumierguy.galagatest.data.SoftBodyObject
import com.thelumierguy.galagatest.data.SoftBodyObjectData
import com.thelumierguy.galagatest.data.SoftBodyObjectType
import com.thelumierguy.galagatest.databinding.*
import com.thelumierguy.galagatest.ui.game.views.bullets.BulletView
import com.thelumierguy.galagatest.ui.game.views.enemyShip.EnemyDetailsCallback
import com.thelumierguy.galagatest.ui.game.views.enemyShip.OnCollisionCallBack
import com.thelumierguy.galagatest.utils.BackgroundMusicManager
import com.thelumierguy.galagatest.utils.goFullScreen
import java.util.*


class MainActivity : AppCompatActivity(), SoftBodyObject.SoftBodyObjectTracker, OnCollisionCallBack,
    EnemyDetailsCallback {

    lateinit var binding: ActivityMainBinding

    val viewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[MainViewModel::class.java]
    }

    lateinit var initScene: SceneContainer<GameInitScreenBinding>

    lateinit var levelCompleteScene: SceneContainer<LevelCompleteSceneBinding>

    lateinit var levelStartWarpScene: SceneContainer<LevelStartWarpSceneBinding>

    lateinit var gameMenuScene: SceneContainer<MainMenuSceneBinding>

    lateinit var youDiedScene: SceneContainer<YouDiedSceneBinding>

    lateinit var gameScene: SceneContainer<GameSceneBinding>

    lateinit var levelStartScene: SceneContainer<LevelStartSceneBinding>

    lateinit var gameOverScene: SceneContainer<GameOverSceneBinding>

    val backgroundMusicManager by lazy {
        BackgroundMusicManager(applicationContext).apply {
            lifecycle.addObserver(this)
        }
    }


    private val transitionManager by lazy {
        TransitionManager()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goFullScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            binding.rootContainer.setPadding(0,
                insets.systemWindowInsetTop,
                0,
                insets.systemWindowInsetBottom)
            insets
        }
        initScenes()
        observeScreenStates()
    }

    private fun initScenes() {
        initScene =
            GameInitScreenBinding.inflate(layoutInflater, binding.root, false).let {
                SceneContainer(it, Scene(binding.rootContainer, it.root))
            }

        gameMenuScene =
            MainMenuSceneBinding.inflate(layoutInflater, binding.root, false).let {
                SceneContainer(it, Scene(binding.rootContainer, it.root))
            }

        levelStartScene =
            LevelStartSceneBinding.inflate(layoutInflater, binding.root, false).let {
                SceneContainer(it, Scene(binding.rootContainer, it.root))
            }

        youDiedScene =
            YouDiedSceneBinding.inflate(layoutInflater, binding.root, false).let {
                SceneContainer(it, Scene(binding.rootContainer, it.root))
            }

        startGameScene()

        gameOverScene =
            GameOverSceneBinding.inflate(layoutInflater, binding.root, false).let {
                SceneContainer(it, Scene(binding.rootContainer, it.root))
            }
    }

    override fun initBulletTracking(softBodyObjectData: SoftBodyObjectData) {
        if (softBodyObjectData.sender == BulletView.Sender.PLAYER) {
            gameScene.binding.enemiesView.checkCollision(softBodyObjectData)
        } else {
            gameScene.binding.spaceShipView.checkCollision(softBodyObjectData)
        }
    }


    override fun cancelTracking(bulletId: UUID, sender: BulletView.Sender) {
        if (sender == BulletView.Sender.PLAYER) {
            gameScene.binding.spaceShipView.removeSoftBodyEntry(bulletId)
        } else {
            gameScene.binding.enemiesView.removeSoftBodyEntry(bulletId)
        }
    }

    override fun onCollision(softBodyObject: SoftBodyObjectData) {
        if (softBodyObject.objectType == SoftBodyObjectType.BULLET) {
            gameScene.binding.bulletView.destroyBullet(softBodyObject.objectId)
        } else {
            gameScene.binding.dropsView.destroyObject(softBodyObject.objectId)
        }
    }

    override fun onAllEliminated(ammoCount: Int) {
        lifecycleScope.launchWhenCreated {
            showLevelCompleteScene(ammoCount)
        }
    }

    private fun showLevelCompleteScene(ammoCount: Int) {
        viewModel.updateUIState(ScreenStates.LevelComplete(ammoCount))
    }

    override fun onCanonReady(enemyX: Float, enemyY: Float) {
        gameScene.binding.bulletView.fire(enemyX, enemyY, BulletView.Sender.ENEMY)
    }

    override fun hasDrop(enemyX: Float, enemyY: Float) {
        gameScene.binding.dropsView.dropGift(enemyX, enemyY)
    }

    fun startGameScene() {
        binding.rootContainer.removeAllViews()
        levelCompleteScene =
            LevelCompleteSceneBinding.inflate(layoutInflater, binding.root, false).let {
                SceneContainer(it, Scene(binding.rootContainer, it.root))
            }

        levelStartWarpScene =
            LevelStartWarpSceneBinding.inflate(layoutInflater, binding.root, false).let {
                SceneContainer(it, Scene(binding.rootContainer, it.root))
            }

        gameScene = GameSceneBinding.inflate(layoutInflater, binding.root, false).let {
            SceneContainer(it, Scene(binding.rootContainer, it.root))
        }
    }

    override fun onGameOver() {
        viewModel.updateUIState(ScreenStates.YouDied)
    }

    fun transitionFromTo(fromScene: Scene, toScene: Scene, transition: Transition) {
        transitionManager.setTransition(fromScene, toScene, transition)
        transitionManager.transitionTo(toScene)
    }

    fun transitionTo(toScene: Scene, transition: Transition) {
        transitionManager.setTransition(toScene, transition)
        transitionManager.transitionTo(toScene)
    }

    override fun onBackPressed() {
        when (viewModel.observeScreenState().value) {
            ScreenStates.GameMenu -> finish()
            else -> viewModel.updateUIState(ScreenStates.GameMenu)
        }
    }

}