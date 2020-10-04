package com.thelumierguy.galagatest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.thelumierguy.galagatest.ui.playership.SpaceShipView
import com.thelumierguy.galagatest.ui.bullets.BulletTracker
import com.thelumierguy.galagatest.ui.bullets.BulletView
import com.thelumierguy.galagatest.ui.enemyShip.EnemiesView
import com.thelumierguy.galagatest.ui.enemyShip.OnCollisionDetector
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class MainActivity : AppCompatActivity(), BulletTracker, OnCollisionDetector {

    val enemiesView: EnemiesView by lazy { findViewById(R.id.enemiesView) }
    val bulletView: BulletView by lazy { findViewById(R.id.bulletView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val spaceShipView = findViewById<SpaceShipView>(R.id.spaceShipView)
        bulletView.bulletTracker = this
        enemiesView.onCollisionDetector = this
        bulletView.setOnClickListener {
            bulletView.shipY = spaceShipView.getShipY()
            bulletView.shipX = spaceShipView.getShipX()
        }
    }

    override fun initBulletTracking(bulletId: UUID, bulletPosition: MutableStateFlow<Pair<Float, Float>>) {
        enemiesView.checkCollision(bulletId,bulletPosition)
    }

    override fun cancelTracking(bulletId: UUID) {
        enemiesView.removeBullet(bulletId)
    }

    override fun onCollision(index: Int) {
        bulletView.destroyBullet(index)
    }
}