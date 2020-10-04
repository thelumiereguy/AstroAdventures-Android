package com.thelumierguy.galagatest

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.thelumierguy.galagatest.ui.bullets.BulletTracker
import com.thelumierguy.galagatest.ui.bullets.BulletView
import com.thelumierguy.galagatest.ui.enemyShip.EnemiesView
import com.thelumierguy.galagatest.ui.enemyShip.OnCollisionDetector
import com.thelumierguy.galagatest.ui.playership.SpaceShipView
import com.thelumierguy.galagatest.utils.AccelerometerManager
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class MainActivity : AppCompatActivity(), BulletTracker, OnCollisionDetector {

    private val enemiesView: EnemiesView by lazy { findViewById(R.id.enemiesView) }
    private val bulletView: BulletView by lazy { findViewById(R.id.bulletView) }
    private val spaceShipView by lazy { findViewById<SpaceShipView>(R.id.spaceShipView) }

    private var accelerometerManager: AccelerometerManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goFullScreen()
        setContentView(R.layout.activity_main)
        bulletView.bulletTracker = this
        enemiesView.onCollisionDetector = this
        bulletView.setOnClickListener {
            bulletView.shipY = spaceShipView.getShipY()
            bulletView.shipX = spaceShipView.getShipX()
        }
        addAccelerometerListener()
    }

    private fun goFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }

    private fun addAccelerometerListener() {
        accelerometerManager = AccelerometerManager(this) { sensorEvent ->
            spaceShipView.processSensorEvents(sensorEvent)
        }
        accelerometerManager?.let {
            lifecycle.addObserver(it)
        }
    }

    override fun initBulletTracking(
        bulletId: UUID,
        bulletPosition: MutableStateFlow<Pair<Float, Float>>,
    ) {
        enemiesView.checkCollision(bulletId, bulletPosition)
    }

    override fun cancelTracking(bulletId: UUID) {
        enemiesView.removeBullet(bulletId)
    }

    override fun onCollision(index: Int) {
        bulletView.destroyBullet(index)
    }
}