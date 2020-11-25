package com.thelumierguy.galagatest.ui.enemyShip.enemy

import android.graphics.*
import android.util.Range
import com.thelumierguy.galagatest.ui.enemyShip.EnemiesView
import kotlin.random.Random


class Enemy(val radius: Float) {

    val drawRect = RectF(
        0F, 0F, 0F, 0F
    )


    var enemyLife = Random.nextInt(1, 5)
    var enemyY = 0F
    var enemyX = 0F

    private val bodyRadius = radius / 2F
    private val bridgeHeight = bodyRadius / 2


    private val paint by lazy {
        Paint().apply {
            color = Color.rgb(
                Random.nextInt(128, 255),
                Random.nextInt(128, 255),
                Random.nextInt(128, 255)
            )
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
            isAntiAlias = false
            isDither = false
            setShadowLayer(4F, 0F, -1F, color)
        }
    }

    fun onHit() {
        enemyLife -= 1
        paint.alpha = 42 * enemyLife
        isVisible = enemyLife > 0
    }


    companion object {
        fun builder(width: Int, positionX: Int, positionY: Int): Enemy {
            return Enemy(width / 20F).apply {
                val boxSize = width / EnemiesView.columnSize.toFloat()
                drawRect.set(
                    boxSize * positionX,
                    boxSize * positionY,
                    boxSize * (positionX + 1),
                    boxSize * (positionY + 1),
                )
                enemyX = drawRect.centerX()
                enemyY = drawRect.centerY()
            }
        }
    }

    var isVisible: Boolean = true


    fun onDraw(canvas: Canvas?) {
        if (isVisible) {
            drawBridge(canvas)
            drawWings(canvas)
            canvas?.drawCircle(enemyX, enemyY, bodyRadius, paint)
        }
    }

    private fun drawWings(canvas: Canvas?) {
        val yStart = enemyY - bodyRadius
        val yEnd = enemyY + bodyRadius
        canvas?.drawLine(enemyX - radius, yStart, enemyX - radius, yEnd, paint)
        canvas?.drawLine(enemyX + radius, yStart, enemyX + radius, yEnd, paint)
    }

    private fun drawBridge(canvas: Canvas?) {
        val path = Path()
        path.moveTo(enemyX, enemyY - bridgeHeight)
        path.lineTo(enemyX - radius, enemyY - 2)
        path.lineTo(enemyX - radius, enemyY + 2)
        path.lineTo(enemyX, enemyY + bridgeHeight)
        path.lineTo(enemyX + radius, enemyY + 2)
        path.lineTo(enemyX + radius, enemyY - 2)
        path.close()
        canvas?.drawPath(path, paint)
    }

    fun checkEnemyYPosition(bulletY: Float): Boolean {
        return Range(enemyX - radius, enemyX + radius).contains(bulletY) && isVisible
    }

}