package com.thelumierguy.astroadventures.ui.game.views.enemyShip.enemyshipdelegates

import android.graphics.Canvas

interface IEnemyShip {
        fun onHit(enemyLife: Int)
    fun onDraw(canvas: Canvas)
    fun translate(offset: Long)
    fun setInitialSize(boxSize: Float, positionX: Int, positionY: Int)
    fun getPositionX(): Float
    fun getPositionY(): Float
    fun hitBoxRadius(): Float
}