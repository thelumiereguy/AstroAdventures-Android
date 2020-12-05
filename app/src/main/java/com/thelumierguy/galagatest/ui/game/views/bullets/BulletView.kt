package com.thelumierguy.galagatest.ui.game.views.bullets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.thelumierguy.galagatest.ui.base.BaseCustomView
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class BulletView(context: Context, attributeSet: AttributeSet? = null) :
    BaseCustomView(context, attributeSet) {

    var bulletTracker: BulletTracker? = null

    private val jetPaint = Paint().apply {
        color = Color.parseColor("#F24423")
        isAntiAlias = false
        strokeWidth = 8F
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 4F
        isDither = false
    }

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    private var bulletStateList = mutableListOf<Bullet>()


    val bulletSize = 40F

    var shipX = 0F
        set(value) {
            field = value
            bulletStateList.add(Bullet(shipX))
            invalidate()
        }

    var shipY = 0F
        set(value) {
            field = measuredHeight - value
        }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            bulletStateList.iterator().forEach {
                it.drawBullet(canvas)
                it.translate()
            }
            bulletStateList.cleanupBullets()
            if (bulletStateList.isNotEmpty()) {
                invalidate()
            }
        }
    }

    private fun MutableList<Bullet>.cleanupBullets() {
        val iterator = iterator()
        while (iterator.hasNext()) {
            val bullet = iterator.next()
            if (bullet.getBulletY() < 0) {
                iterator.remove()
                invalidate()
            }
        }
    }

    fun destroyBullet(index: Int) {
        post {
            if (index < bulletStateList.size) {
                val bulletState = bulletStateList[index]
                bulletStateList.removeAll { it == bulletState }
            }
            invalidate()
        }
    }


    inner class Bullet(private val bulletX: Float) {

        val id = UUID.randomUUID()

        private var bulletY = shipY

        fun getBulletY() = bulletY

        private var bulletPosition =
            MutableStateFlow(BulletCoordinates(bulletX, bulletY))

        init {
            bulletTracker?.initBulletTracking(id, bulletPosition)
        }


        fun drawBullet(canvas: Canvas) {
            if (bulletY > 0) {
                canvas.drawLine(
                    bulletX,
                    bulletY - bulletSize,
                    bulletX,
                    bulletY,
                    jetPaint
                )
            }
        }

        fun translate() {
            bulletY -= 10
            bulletPosition.value = BulletCoordinates(bulletX, bulletY)
            if (bulletY < 0) {
                bulletTracker?.cancelTracking(id)
            }
        }
    }

    override fun hasOverlappingRendering(): Boolean = false
}

interface BulletTracker {
    fun initBulletTracking(bulletId: UUID, bulletPosition: MutableStateFlow<BulletCoordinates>)
    fun cancelTracking(bulletId: UUID)
}