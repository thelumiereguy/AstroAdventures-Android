package com.thelumierguy.galagatest.ui.game.views.bullets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.thelumierguy.galagatest.R
import com.thelumierguy.galagatest.ui.base.BaseCustomView
import com.thelumierguy.galagatest.utils.SoundManager
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class BulletView(context: Context, attributeSet: AttributeSet? = null) :
    BaseCustomView(context, attributeSet) {

    private val fireSoundManager by lazy { SoundManager(R.raw.player_bullet_sound, context) }

    var bulletTracker: BulletTracker? = null

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    private var bulletStateList = mutableListOf<Bullet>()


    val bulletSize = 40F

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
        private val sender: BulletSender,
    ) {

        val id = UUID.randomUUID()

        private var bulletY = shipY

        fun getBulletY() = bulletY

        private var bulletPosition =
            MutableStateFlow(BulletCoordinates(bulletX, bulletY))

        private val jetPaint = Paint().apply {
            color = if (sender == BulletSender.PLAYER) {
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

        init {
            bulletTracker?.initBulletTracking(id, bulletPosition, sender)
        }


        fun drawBullet(canvas: Canvas) {
            if (bulletY > 0 && bulletY < measuredHeight) {
                if (sender == BulletSender.PLAYER) {
                    canvas.drawLine(
                        bulletX,
                        bulletY - bulletSize,
                        bulletX,
                        bulletY,
                        jetPaint
                    )
                } else {
                    canvas.drawLine(
                        bulletX,
                        bulletY,
                        bulletX,
                        bulletY - bulletSize,
                        jetPaint
                    )
                }
            }
        }

        fun translate() {
            if (sender == BulletSender.PLAYER) {
                bulletY -= 10
                if (bulletY < 0) {
                    bulletTracker?.cancelTracking(id)
                }
            } else {
                bulletY += 10
                if (bulletY > measuredHeight) {
                    bulletTracker?.cancelTracking(id)
                }
            }
            bulletPosition.value = BulletCoordinates(bulletX, bulletY)

        }
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

    fun fire(x: Float, y: Float, sender: BulletSender) {
        fireSoundManager.play()
        if (sender == BulletSender.PLAYER) {
            bulletStateList.add(Bullet(x, measuredHeight - y, sender))
        } else {
            bulletStateList.add(Bullet(x, y, sender))
        }
        invalidate()
    }

    enum class BulletSender {
        PLAYER,
        ENEMY
    }
}

interface BulletTracker {
    fun initBulletTracking(
        bulletId: UUID,
        bulletPosition: MutableStateFlow<BulletCoordinates>,
        sender: BulletView.BulletSender,
    )

    fun cancelTracking(bulletId: UUID)
}