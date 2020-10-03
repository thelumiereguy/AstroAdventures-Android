package com.thelumierguy.galagatest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.thelumierguy.galagatest.ui.BulletTracker
import com.thelumierguy.galagatest.ui.BulletView
import com.thelumierguy.galagatest.ui.EnemiesView
import com.thelumierguy.galagatest.ui.SpaceShipView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity(), BulletTracker {

    val enemiesView: EnemiesView by lazy { findViewById(R.id.enemiesView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val spaceShipView = findViewById<SpaceShipView>(R.id.spaceShipView)
        val bulletView = findViewById<BulletView>(R.id.bulletView)
        bulletView.bulletTracker = this
        bulletView.setOnClickListener {
            bulletView.shipY = spaceShipView.getShipY()
            bulletView.shipX = spaceShipView.getShipX()
        }
    }

    override fun initBulletTracking(bulletPosition: MutableStateFlow<Pair<Float, Float>>) {
        enemiesView.checkCollision(bulletPosition)
    }

    override fun cancelTracking(bulletPosition: MutableStateFlow<Pair<Float, Float>>) {
        enemiesView.removeBullet(bulletPosition)
    }
}