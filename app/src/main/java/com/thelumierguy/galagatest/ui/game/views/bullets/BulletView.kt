package com.thelumierguy.galagatest.ui.game.views.bullets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.thelumierguy.galagatest.R
import com.thelumierguy.galagatest.data.SoftBodyObject
import com.thelumierguy.galagatest.data.SoftBodyObjectType
import com.thelumierguy.galagatest.ui.base.BaseCustomView
import com.thelumierguy.galagatest.utils.SoundManager
import java.util.*

class BulletView(context: Context, attributeSet: AttributeSet? = null) :
    BaseCustomView(context, attributeSet) {

    private val fireSoundManager by lazy { SoundManager(R.raw.player_bullet_sound, context) }

    var softBodyObjectTracker: SoftBodyObject.SoftBodyObjectTracker? = null

    enum class Sender {
        PLAYER,
        ENEMY
    }

    override fun hasOverlappingRendering(): Boolean = false

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode)
            fireSoundManager.init()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        fireSoundManager.release()
    }

    fun fire(x: Float, y: Float, sender: Sender) {
        fireSoundManager.play()
        if (sender == Sender.PLAYER) {
            bulletStateList.add(Bullet(x,
                measuredHeight - y,
                sender,
                measuredHeight,
                softBodyObjectTracker))
        } else {
            bulletStateList.add(Bullet(x, y, sender, measuredHeight, softBodyObjectTracker))
        }
        invalidate()
    }

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    private var bulletStateList = mutableListOf<Bullet>()


    val bulletSize = 40F

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            bulletStateList.iterator().forEach {
                it.drawObject(canvas)
                it.translateObject()
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
            if (bullet.getObjectY() < 0 && bullet.getObjectY() > measuredHeight) {
                iterator.remove()
                invalidate()
            }
        }
    }

    fun destroyBullet(id: UUID) {
        post {
            val bulletState = bulletStateList.find {
                it.id == id
            }
            bulletStateList.removeAll { it == bulletState }
        }
        invalidate()
    }


    inner class Bullet(
        private val bulletX: Float,
        shipY: Float,
        private val sender: Sender,
        maxHeight: Int,
        bulletTracker: SoftBodyObjectTracker?,
    ) : SoftBodyObject(bulletX,
        shipY,
        sender,
        maxHeight,
        bulletTracker,
        SoftBodyObjectType.BULLET) {


        override val speed: Int = 10

        private val jetPaint = Paint().apply {
            color = if (sender == Sender.PLAYER) {
                ResourcesCompat.getColor(context.resources,
                    R.color.bulletColor,
                    null)
            } else {
                Color.RED
            }
            isAntiAlias = false
            strokeWidth = 8F
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 4F
            isDither = false
        }


        override fun drawObject(canvas: Canvas) {
            if (sender == Sender.PLAYER) {
                canvas.drawLine(
                    bulletX,
                    getObjectY() - bulletSize,
                    bulletX,
                    getObjectY(),
                    jetPaint
                )
            } else {
                canvas.drawLine(
                    bulletX,
                    getObjectY(),
                    bulletX,
                    getObjectY() - bulletSize,
                    jetPaint
                )
            }
        }

        override fun translateObject() {
            translate()
            bulletPosition.value = SoftBodyCoordinates(bulletX, getObjectY())

        }
    }

}