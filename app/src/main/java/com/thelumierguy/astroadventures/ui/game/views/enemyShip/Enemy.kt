package com.thelumierguy.astroadventures.ui.game.views.enemyShip

import android.graphics.Canvas
import android.util.Range
import com.thelumierguy.astroadventures.data.Score
import com.thelumierguy.astroadventures.ui.game.views.enemyShip.enemyshipdelegates.AlienShip
import com.thelumierguy.astroadventures.ui.game.views.enemyShip.enemyshipdelegates.CapitalShip
import com.thelumierguy.astroadventures.ui.game.views.enemyShip.enemyshipdelegates.IEnemyShip
import com.thelumierguy.astroadventures.ui.game.views.enemyShip.enemyshipdelegates.TieFighter
import kotlin.random.Random


class Enemy {

    var isVisible: Boolean = true

    val hasDrops: Boolean by lazy {
        val chance = Random.nextDouble(0.0, 1.0)
        chance > 0.9
    }

    var enemyLife = Random.nextInt(1, 4)

    val enemyDelegate: IEnemyShip by lazy {
        when (enemyLife) {
            1 -> CapitalShip()
            2 -> AlienShip()
            else -> TieFighter()
        }
    }

    private val points = enemyLife * 25L


    val enemyX: Float
        get() = enemyDelegate.getPositionX()

    val enemyY: Float
        get() = enemyDelegate.getPositionY()

    val hitBoxRadius: Float
        get() = enemyDelegate.hitBoxRadius()

    fun onHit() {
        enemyLife--
        if (enemyLife <= 0) {
            Score.updateScore(points)
        }
        enemyDelegate.onHit(enemyLife)
        isVisible = enemyLife > 0
    }


    companion object {
        fun builder(columnSize: Int, width: Int, positionX: Int, positionY: Int): Enemy {
            return Enemy().apply {
                val boxSize = width / columnSize.toFloat()
                enemyDelegate.setInitialSize(boxSize, positionX, positionY)
            }
        }
    }

    fun onDraw(canvas: Canvas?) {
        if (isVisible) {
            canvas?.let {
                enemyDelegate.onDraw(canvas)
            }
        }
    }

    fun checkEnemyYPosition(bulletY: Float): Boolean {
        return Range(enemyDelegate.getPositionY() - enemyDelegate.hitBoxRadius(),
            enemyDelegate.getPositionY() + enemyDelegate.hitBoxRadius()).contains(bulletY) && isVisible
    }


    fun translate(offset: Long) {
        enemyDelegate.translate(offset)
    }

}