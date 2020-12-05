package com.thelumierguy.galagatest.ui.game.views.enemyShip

import android.graphics.Canvas
import android.util.Range
import com.thelumierguy.galagatest.ui.game.views.enemyShip.enemyshipdelegates.AlienShip
import com.thelumierguy.galagatest.ui.game.views.enemyShip.enemyshipdelegates.CapitalShip
import com.thelumierguy.galagatest.ui.game.views.enemyShip.enemyshipdelegates.IEnemyShip
import com.thelumierguy.galagatest.ui.game.views.enemyShip.enemyshipdelegates.TieFighter
import kotlin.random.Random


class Enemy {

    var isVisible: Boolean = true

    var enemyLife = Random.nextInt(1, 4)

    val enemyDelegate: IEnemyShip by lazy {
        when (enemyLife) {
            1 -> TieFighter()
            2 -> AlienShip()
            else -> CapitalShip()
        }
    }

    val enemyX: Float
        get() = enemyDelegate.getPositionX()

    val enemyY: Float
        get() = enemyDelegate.getPositionY()

    val hitBoxRadius: Float
        get() = enemyDelegate.hitBoxRadius()

    fun onHit() {
        enemyLife--
        enemyDelegate.onHit(enemyLife)
        isVisible = enemyLife > 0
    }


    companion object {
        fun builder(width: Int, positionX: Int, positionY: Int): Enemy {
            return Enemy().apply {
                val boxSize = width / EnemyClusterView.columnSize.toFloat()
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
        return Range(enemyDelegate.getPositionY() - enemyDelegate.hitBoxRadius(), enemyDelegate.getPositionY() + enemyDelegate.hitBoxRadius()).contains(bulletY) && isVisible
    }


    fun translate(offset: Long) {
        enemyDelegate.translate(offset)
    }

}