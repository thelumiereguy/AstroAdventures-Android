package com.thelumierguy.galagatest.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.thelumierguy.galagatest.R
import com.thelumierguy.galagatest.ui.observeScreenStates
import com.thelumierguy.galagatest.ui.views.bullets.BulletCoordinates
import com.thelumierguy.galagatest.ui.views.bullets.BulletTracker
import com.thelumierguy.galagatest.ui.views.bullets.BulletView
import com.thelumierguy.galagatest.ui.views.enemyShip.EnemyClusterView
import com.thelumierguy.galagatest.ui.views.enemyShip.OnCollisionDetector
import com.thelumierguy.galagatest.ui.views.playership.SpaceShipView
import com.thelumierguy.galagatest.utils.AccelerometerManager
import com.thelumierguy.galagatest.utils.goFullScreen
import com.thelumierguy.galagatest.viewmodel.MainViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class MainActivity : AppCompatActivity(), BulletTracker, OnCollisionDetector {

    private val enemyClusterView: EnemyClusterView by lazy { findViewById(R.id.enemiesView) }
    private val bulletView: BulletView by lazy { findViewById(R.id.bulletView) }
    private val spaceShipView by lazy { findViewById<SpaceShipView>(R.id.spaceShipView) }

    private var accelerometerManager: AccelerometerManager? = null

    val mainViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goFullScreen()
        setContentView(R.layout.activity_main)
        observeScreenStates()
        bulletView.bulletTracker = this
        enemyClusterView.onCollisionDetector = this
        bulletView.setOnClickListener {
            bulletView.shipY = spaceShipView.getShipY()
            bulletView.shipX = spaceShipView.getShipX()
        }
        addAccelerometerListener()
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
        bulletPosition: MutableStateFlow<BulletCoordinates>,
    ) {
        enemyClusterView.checkCollision(bulletId, bulletPosition)
    }

    override fun cancelTracking(bulletId: UUID) {
        enemyClusterView.removeBullet(bulletId)
    }

    override fun onCollision(index: Int) {
        bulletView.destroyBullet(index)
    }
}