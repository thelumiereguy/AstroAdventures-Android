package com.thelumierguy.astroadventures.ui.game.views.enemyShip.enemyshipdelegates

import android.graphics.*
import com.thelumierguy.astroadventures.ui.game.views.enemyShip.EnemyClusterView
import kotlin.random.Random


class CapitalShip : IEnemyShip {

    private val drawRect = RectF(
        0F, 0F, 0F, 0F
    )

    var enemyY = 0F

    var enemyX = 0F

    private var coreRadius = 0F

    private var bridgeHeight = 0F

    private val drawPath = Path()

    private val mainColor = Color.rgb(
        Random.nextInt(128, 255),
        Random.nextInt(128, 255),
        Random.nextInt(128, 255)
    )

    private val paint by lazy {
        Paint().apply {
            color = mainColor
            isAntiAlias = false
            isDither = false
        }
    }

    private val strokePaint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            color = mainColor
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
            isAntiAlias = false
            strokeWidth = 5F
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isDither = false
        }
    }


    override fun onHit(enemyLife: Int) {
        val alpha = 70 * enemyLife
        paint.alpha = alpha
        strokePaint.alpha = alpha
    }


    override fun onDraw(canvas: Canvas) {
        drawBody(canvas)
    }

    private fun drawBody(canvas: Canvas) {
        drawPath.reset()
        val topHeight = coreRadius / 3
        val wingWidth = coreRadius / 2
        drawWingsAndBody(topHeight, wingWidth, canvas)
        drawGuns(canvas, wingWidth, topHeight)
    }

    private fun drawGuns(
        canvas: Canvas,
        wingWidth: Float,
        topHeight: Float,
    ) {
        canvas.drawLine(enemyX - wingWidth,
            enemyY - topHeight,
            enemyX - coreRadius,
            enemyY - topHeight,
            strokePaint)
        canvas.drawLine(enemyX + wingWidth,
            enemyY - topHeight,
            enemyX + coreRadius,
            enemyY - topHeight,
            strokePaint)

        canvas.drawLine(
            enemyX - coreRadius,
            enemyY - topHeight,
            enemyX - coreRadius,
            enemyY + topHeight,
            strokePaint
        )

        canvas.drawLine(
            enemyX + coreRadius,
            enemyY - topHeight,
            enemyX + coreRadius,
            enemyY + topHeight,
            strokePaint
        )
    }

    private fun drawWingsAndBody(
        topHeight: Float,
        wingWidth: Float,
        canvas: Canvas,
    ) {
        val roundPartTopPoint = enemyY - (2 * topHeight)
        drawPath.moveTo(enemyX - topHeight, roundPartTopPoint)
        drawPath.quadTo(enemyX, enemyY - coreRadius, enemyX + topHeight, roundPartTopPoint)

        //Right wing *badum tss*
        val wingTopPoint = enemyY - topHeight
        drawPath.lineTo(enemyX + (2 * topHeight), wingTopPoint)
        drawPath.lineTo(enemyX + wingWidth, enemyY)
        drawPath.lineTo(enemyX + wingWidth, enemyY)
        drawPath.lineTo(enemyX, enemyY + coreRadius)

        drawPath.lineTo(enemyX - wingWidth, enemyY)
        drawPath.lineTo(enemyX - wingWidth, enemyY)
        drawPath.lineTo(enemyX - (2 * topHeight), wingTopPoint)

        drawPath.close()

        canvas.drawPath(drawPath, paint)
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