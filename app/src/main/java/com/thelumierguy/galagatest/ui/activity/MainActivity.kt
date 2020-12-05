package com.thelumierguy.galagatest.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.thelumierguy.galagatest.R
import com.thelumierguy.galagatest.databinding.ActivityMainBinding
import com.thelumierguy.galagatest.ui.observeScreenStates
import com.thelumierguy.galagatest.ui.viewmodel.MainViewModel
import com.thelumierguy.galagatest.ui.views.bullets.BulletCoordinates
import com.thelumierguy.galagatest.ui.views.bullets.BulletTracker
import com.thelumierguy.galagatest.ui.views.bullets.BulletView
import com.thelumierguy.galagatest.ui.views.enemyShip.EnemyClusterView
import com.thelumierguy.galagatest.ui.views.enemyShip.OnCollisionDetector
import com.thelumierguy.galagatest.ui.views.playership.SpaceShipView
import com.thelumierguy.galagatest.utils.AccelerometerManager
import com.thelumierguy.galagatest.utils.MusicManager
import com.thelumierguy.galagatest.utils.goFullScreen
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class MainActivity : AppCompatActivity(), BulletTracker, OnCollisionDetector {

    private lateinit var binding: ActivityMainBinding

    private var accelerometerManager: AccelerometerManager? = null

    val mainViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goFullScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeScreenStates()
        binding.bulletView.bulletTracker = this
        binding.enemiesView.onCollisionDetector = this
        binding.bulletView.setOnClickListener {
            binding.bulletView.shipY = binding.spaceShipView.getShipY()
            binding.bulletView.shipX = binding.spaceShipView.getShipX()
        }
        addAccelerometerListener()
        startMusic()
    }

    private fun addAccelerometerListener() {
        accelerometerManager = AccelerometerManager(applicationContext) { sensorEvent ->
            binding.spaceShipView.processSensorEvents(sensorEvent)
        }
        accelerometerManager?.let {
            lifecycle.addObserver(it)
        }
    }

    private fun startMusic() {
        MusicManager(applicationContext).run {
            lifecycle.addObserver(this)
        }
    }

    override fun initBulletTracking(
        bulletId: UUID,
        bulletPosition: MutableStateFlow<BulletCoordinates>,
    ) {
        binding.enemiesView.checkCollision(bulletId, bulletPosition)
    }

    override fun cancelTracking(bulletId: UUID) {
        binding.enemiesView.removeBullet(bulletId)
    }

    override fun onCollision(index: Int) {
        binding.bulletView.destroyBullet(index)
    }
}