package com.thelumierguy.astroadventures.ui.game.views.drops

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.thelumierguy.astroadventures.R
import com.thelumierguy.astroadventures.data.DropType
import com.thelumierguy.astroadventures.data.SoftBodyObject
import com.thelumierguy.astroadventures.data.SoftBodyObjectType
import com.thelumierguy.astroadventures.ui.base.BaseCustomView
import com.thelumierguy.astroadventures.ui.game.views.bullets.BulletView
import com.thelumierguy.astroadventures.ui.game.views.bullets.SoftBodyCoordinates
import com.thelumierguy.astroadventures.utils.SoundManager
import java.util.*
import kotlin.random.Random

class DropsView(context: Context, attributeSet: AttributeSet? = null) :
    BaseCustomView(context, attributeSet) {

    private val fireSoundManager by lazy { SoundManager(R.raw.player_bullet_sound, context) }

    var softBodyObjectTracker: SoftBodyObject.SoftBodyObjectTracker? = null


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

    fun dropGift(x: Float, y: Float) {
        fireSoundManager.play()
        ammoDropsList.add(AmmoDrop(bulletX = x,
            shipY = y,
            maxHeight = measuredHeight,
            bulletTracker = softBodyObjectTracker,
            dropType = SoftBodyObjectType.DROP(DropType.Ammo(Random.nextInt(10, 30)))))
        invalidate()
    }

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    private var ammoDropsList = mutableListOf<AmmoDrop>()


    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            ammoDropsList.iterator().forEach {
                it.drawObject(canvas)
                it.translateObject()
            }
            ammoDropsList.cleanupBullets()
            if (ammoDropsList.isNotEmpty()) {
                invalidate()
            }
        }
    }

    private fun MutableList<AmmoDrop>.cleanupBullets() {
        val iterator = iterator()
        while (iterator.hasNext()) {
            val bullet = iterator.next()
            if (bullet.getObjectY() < 0) {
                iterator.remove()
                invalidate()
            }
        }
    }

    fun destroyObject(id: UUID) {
        post {
            val objectState = ammoDropsList.find {
                it.id == id
            }
            ammoDropsList.removeAll { it == objectState }
        }
        invalidate()
    }

    inner class AmmoDrop(
        private val bulletX: Float,
        shipY: Float,
        sender: BulletView.Sender = BulletView.Sender.ENEMY,
        maxHeight: Int,
        bulletTracker: SoftBodyObjectTracker?,
        dropType: SoftBodyObjectType,
    ) : SoftBodyObject(bulletX, shipY, sender, maxHeight, bulletTracker, dropType) {

        override val speed: Int = 10

        private val ammoDropPaint = Paint().apply {
            color = ResourcesCompat.getColor(context.resources,
                R.color.shipHighLightColor,
                null)
            isAntiAlias = false
            strokeWidth = 5F
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 4F
            isDither = false
        }

        private val midCirclePaint = Paint().apply {
            color = Color.RED
            isAntiAlias = false
            isDither = false
        }


        private val capsuleLength = maxHeight * 0.02F
        private val capsuleLHeight = maxHeight * 0.01F

        private val drawRect = RectF(bulletX - capsuleLength,
            shipY - capsuleLHeight,
            bulletX + capsuleLength,
            shipY + capsuleLHeight)

        override fun drawObject(canvas: Canvas) {
            if (getObjectY() > 0 && getObjectY() < measuredHeight) {
                canvas.drawRoundRect(drawRect, capsuleLHeight, capsuleLHeight, ammoDropPaint)
                canvas.drawCircle(bulletX, getObjectY(), capsuleLHeight / 2, midCirclePaint)
            }
        }

        override fun translateObject() {
            translate()
            drawRect.offset(0F, speed.toFloat())
            bulletPosition.value = SoftBodyCoordinates(bulletX, getObjectY())

        }
    }

}