package com.thelumierguy.galagatest.ui.views.enemyShip.enemy.enemyshipdelegates

import android.graphics.*
import com.thelumierguy.galagatest.ui.views.enemyShip.EnemyClusterView
import kotlin.random.Random


class TieFighter : IEnemyShip {

    private val drawRect = RectF(
        0F, 0F, 0F, 0F
    )

    var enemyY = 0F

    var enemyX = 0F

    private var coreRadius = 0F

    private var bridgeHeight = 0F


    private val mainColor = Color.rgb(
        Random.nextInt(128, 255),
        Random.nextInt(128, 255),
        Random.nextInt(128, 255)
    )

    private val paint by lazy {
        Paint().apply {
            color = mainColor
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
            isAntiAlias = false
            isDither = false
//            setShadowLayer(14F, 0F, 0F, color)
        }
    }


    private val strokePaint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            color = mainColor
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
            isAntiAlias = false
            strokeWidth = 2F
            isDither = false
//            setShadowLayer(14F, 0F, 0F, color)
        }
    }


    override fun onHit(enemyLife: Int) {
        paint.alpha = 70 * enemyLife
    }


    override fun onDraw(canvas: Canvas) {
        drawBridge(canvas)
        drawWings(canvas)
        canvas.drawCircle(enemyX, enemyY, coreRadius / 2F, paint)
    }

    private fun drawWings(canvas: Canvas?) {
        val yStart = enemyY - coreRadius
        val yEnd = enemyY + coreRadius
        canvas?.drawLine(enemyX - coreRadius, yStart, enemyX - coreRadius, yEnd, strokePaint)
        canvas?.drawLine(enemyX + coreRadius, yStart, enemyX + coreRadius, yEnd, strokePaint)
    }

    private fun drawBridge(canvas: Canvas?) {
        val path = Path()
        path.moveTo(enemyX, enemyY - bridgeHeight)
        path.lineTo(enemyX - coreRadius, enemyY - 2)
        path.lineTo(enemyX - coreRadius, enemyY + 2)
        path.lineTo(enemyX, enemyY + bridgeHeight)
        path.lineTo(enemyX + coreRadius, enemyY + 2)
        path.lineTo(enemyX + coreRadius, enemyY - 2)
        path.close()
        canvas?.drawPath(path, paint)
    }


    override fun translate(offset: Long) {
        enemyY += EnemyClusterView.speed
        drawRect.offset(0F, EnemyClusterView.speed)
    }

    override fun setInitialSize(boxSize: Float, positionX: Int, positionY: Int) {
        drawRect.set(
            boxSize * positionX,
            boxSize * positionY,
            boxSize * (positionX + 1),
            boxSize * (positionY + 1),
        )
        enemyX = drawRect.centerX()
        enemyY = drawRect.centerY()
        coreRadius = drawRect.width() / 4F
        bridgeHeight = coreRadius / 6
    }

    override fun getPositionX(): Float = enemyX
    override fun getPositionY(): Float = enemyY

    override fun hitBoxRadius(): Float = coreRadius
}