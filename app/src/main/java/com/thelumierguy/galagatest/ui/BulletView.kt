package com.thelumierguy.galagatest.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.thelumierguy.galagatest.utils.CustomLifeCycleOwner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow

@ExperimentalCoroutinesApi
class BulletView(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {

    private val lifeCycleOwner by lazy { CustomLifeCycleOwner() }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        lifeCycleOwner.startListening()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lifeCycleOwner.stopListening()
    }

    var bulletTracker: BulletTracker? = null

    private val jetPaint = Paint().apply {
        color = Color.parseColor("#F24423")
        isAntiAlias = false
        strokeWidth = 8F
        isDither = false
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
            bulletStateList.forEach {
                it.fire(canvas)
                it.updatePosition()
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
            if (bullet.bulletY < 0) {
                iterator.remove()
            }
        }
    }


    inner class Bullet(val bulletX: Float) {

        var bulletY = shipY

        private var bulletPosition =
            MutableStateFlow(Pair(bulletX, bulletY))

        init {
            bulletTracker?.initBulletTracking(bulletPosition)
        }


        fun fire(canvas: Canvas) {
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

        fun updatePosition() {
            bulletY -= 10
            bulletPosition.value = Pair(shipX, shipY)
            if (bulletY < 0) {
                bulletTracker?.cancelTracking(bulletPosition)
            }
        }
    }

}

interface BulletTracker {
    fun initBulletTracking(bulletPosition: MutableStateFlow<Pair<Float, Float>>)
    fun cancelTracking(bulletPosition: MutableStateFlow<Pair<Float, Float>>)
}