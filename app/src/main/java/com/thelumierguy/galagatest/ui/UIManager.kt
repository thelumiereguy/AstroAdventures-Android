package com.thelumierguy.galagatest.ui

import androidx.lifecycle.lifecycleScope
import com.thelumierguy.galagatest.ui.activity.MainActivity
import com.thelumierguy.galagatest.ui.viewmodel.ScreenStates
import kotlinx.coroutines.flow.collect


fun MainActivity.observeScreenStates() {
    lifecycleScope.launchWhenCreated {
        mainViewModel.observeScreenState().collect {
            when (it) {
                ScreenStates.APP_INIT -> {
//                    transitionToScene(appInitScene)
                }
                ScreenStates.GAME_MENU -> {
//                    transitionToScene(gameMenuScene)
//                    gameMenuScene.sceneRoot.findViewById<LogoView>(R.id.imageView)?.enableTinkling =
//                        true
//                    gameMenuScene.sceneRoot.findViewById<BlinkingImage>(R.id.iv_text)
//                        ?.startBlinking()
                }
                ScreenStates.START_GAME -> {
//                    transitionToScene(startGameScene)
//                    startGameScene.sceneRoot.findViewById<ImageView>(R.id.iv_pause)
//                        .setOnClickListener {
//                            onBackPressed()
//                        }
                }
            }
        }
    }
}