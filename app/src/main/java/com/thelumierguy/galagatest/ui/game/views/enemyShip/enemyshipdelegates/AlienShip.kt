package com.thelumierguy.galagatest.ui.game.views.enemyShip.enemyshipdelegates

import android.graphics.*
import com.thelumierguy.galagatest.ui.game.views.enemyShip.EnemyClusterView
import kotlin.random.Random


class AlienShip : IEnemyShip {
    private val drawRect = RectF(
        0F, 0F, 0F, 0F
    )

    var enemyY = 0F

    var enemyX = 0F

    private var coreRadius = 0F

    private var bridgeHeight = 0F

    private val drawPath = Path()

    var rotationOffset = 0F


    private val mainColor = Color.rgb(
        Random.nextInt(128, 255),
        Random.nextInt(128, 255),
        Random.nextInt(128, 255)
    )

    private val paint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            color = mainColor
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
            isAntiAlias = false
            strokeWidth = 10F
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isDither = false
        }
    }


    override fun onHit(enemyLife: Int) {
        paint.alpha = 70 * enemyLife
    }


    override fun onDraw(canvas: Canvas) {
        val bodyRadius = coreRadius / 4F
        drawPath.reset()
        drawPath.moveTo(enemyX, enemyY)
        //bottom
        drawPath.quadTo(enemyX - bodyRadius, enemyY + bodyRadius, enemyX, enemyY + (2F * bodyRadius))
        drawPath.quadTo(enemyX + bodyRadius,
            enemyY + bodyRadius,
            enemyX,
            enemyY + (coreRadius))

        drawPath.moveTo(enemyX, enemyY)

        //top
        drawPath.quadTo(enemyX + bodyRadius, enemyY - bodyRadius, enemyX, enemyY - (2F * bodyRadius))
        drawPath.quadTo(enemyX - bodyRadius,
            enemyY - bodyRadius ,
            enemyX,
            enemyY - (coreRadius))


        drawPath.moveTo(enemyX, enemyY)

        //left
        drawPath.quadTo(enemyX - bodyRadius, enemyY + bodyRadius, enemyX - (2F * bodyRadius), enemyY)
        drawPath.quadTo(enemyX - bodyRadius,
            enemyY - bodyRadius,
            enemyX - ( coreRadius),
            enemyY)

        drawPath.moveTo(enemyX, enemyY)

        //right
        drawPath.quadTo(enemyX + bodyRadius, enemyY + bodyRadius, enemyX + (2F * bodyRadius), enemyY)
        drawPath.quadTo(enemyX + bodyRadius ,
            enemyY - bodyRadius,
            enemyX + (coreRadius),
            enemyY)

        canvas.drawPath(drawPath, paint)
    }


    override fun translate(offset: Long) {
        enemyY += EnemyClusterView.speed
        drawRect.offset(0F, EnemyClusterView.speed)
        rotationOffset = offset % 90F
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