package com.thelumierguy.galagatest.ui.enemyShip

import android.graphics.Canvas
import android.graphics.RectF

interface EnemyAirShip {
    fun init()
    fun draw(canvas: Canvas)
}

interface GetAirShipData {
    fun getAirshipDrawRect(): RectF
    fun getX(): Float
    fun getY(): Float
}