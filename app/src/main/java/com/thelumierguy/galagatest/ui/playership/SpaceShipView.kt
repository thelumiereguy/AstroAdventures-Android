package com.thelumierguy.galagatest.ui.playership

import android.content.Context
import android.graphics.*
import android.graphics.drawable.PictureDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import com.thelumierguy.galagatest.ui.base.BaseCustomView
import kotlin.math.roundToInt


class SpaceShipView(context: Context, attributeSet: AttributeSet? = null) :
    BaseCustomView(context, attributeSet) {

    private var currentShipPosition: Float = 0F
    private val bodyPaint = Paint().apply {
        color = Color.parseColor("#DEDEDE")
        isAntiAlias = false
        isDither = false
    }

    private val bodyPaintStroke = Paint().apply {
        color = Color.parseColor("#DEDEDE")
        style = Paint.Style.STROKE
        isAntiAlias = false
        isDither = false
    }


    private val wingsPaintOutline = Paint().apply {
        color = Color.parseColor("#0069DE")
        style = Paint.Style.STROKE
        strokeWidth = 2F
        isAntiAlias = false
        isDither = false
    }

    private val jetPaint = Paint().apply {
        color = Color.parseColor("#F24423")
        isAntiAlias = false
        strokeWidth = 8F
        isDither = false
    }

    private var streamLinedTopPoint = 0f
    private var bodyTopPoint = 0f
    private var wingWidth = 0F
    private var halfWidth = 0F
    private var halfHeight = 0F
    private var missileSize = 0F


    private lateinit var spaceShipPicture: Picture
    private lateinit var pictureDrawable: PictureDrawable

    private var displayRect = Rect()

    private fun initPicture() {
        spaceShipPicture = Picture()
        pictureDrawable = PictureDrawable(spaceShipPicture)
        val canvas = spaceShipPicture.beginRecording(measuredWidth, measuredHeight)
        canvas.let {
            drawExhaust(it)
            drawStreamlinedBody(it)
            drawBody(it)
            drawMisc(it)
            drawShipWings(it)

        }
        spaceShipPicture.endRecording()

        pictureDrawable = PictureDrawable(spaceShipPicture)

        postInvalidate()
    }

    override fun onDraw(canvas: Canvas?) {
//        if (isInEditMode) {
//            return
//        }
        canvas?.let {
            pictureDrawable.bounds = displayRect
            pictureDrawable.draw(canvas)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        halfWidth = w / 2F
        getDrawingRect(displayRect)
        halfHeight = h / 2F
        currentShipPosition = halfWidth
        streamLinedTopPoint = h / 4F
        bodyTopPoint = h / 3F
        wingWidth = w / 15F
        missileSize = h / 8F
        initPicture()
    }

    private fun drawStreamlinedBody(it: Canvas) {
        bodyPaintStroke.strokeWidth = 10F
        it.drawLine(
            halfWidth,
            streamLinedTopPoint,
            halfWidth,
            measuredHeight - streamLinedTopPoint,
            bodyPaintStroke
        )
    }

    fun getShipX() = currentShipPosition


    private fun drawBody(it: Canvas) {
        bodyPaintStroke.strokeWidth = 24F
        it.drawLine(
            halfWidth,
            bodyTopPoint,
            halfWidth,
            measuredHeight - bodyTopPoint,
            bodyPaintStroke
        )
    }

    private fun drawMisc(canvas: Canvas) {
        var startY = halfHeight + bodyTopPoint
        var startX = halfWidth - wingWidth
        canvas.drawMissile(startX, startY)

        startX = halfWidth + wingWidth
        canvas.drawMissile(startX, startY)

        startX = (halfWidth - wingWidth / 2)
        startY = (halfHeight + bodyTopPoint / 3F)
        canvas.drawMissile(startX, startY)

        startX = (halfWidth + wingWidth / 2)
        canvas.drawMissile(startX, startY)
    }

    private fun drawExhaust(canvas: Canvas) {
        val path = Path()

        val topPoint = halfHeight + streamLinedTopPoint / 2

        path.moveTo(
            halfWidth,
            topPoint
        ) // Top

        path.lineTo(
            halfWidth - wingWidth / 10,
            topPoint
        )

        path.lineTo(
            halfWidth - wingWidth / 5,
            halfHeight + streamLinedTopPoint
        )

        path.lineTo(
            halfWidth,
            measuredHeight - bodyTopPoint
        )

        path.moveTo(
            halfWidth + wingWidth / 10,
            topPoint
        ) // Top

        path.lineTo(
            halfWidth + wingWidth / 5,
            halfHeight + streamLinedTopPoint
        )

        path.lineTo(
            halfWidth,
            measuredHeight - bodyTopPoint
        )

        path.close()

        canvas.drawPath(path, jetPaint)
    }


    private fun Canvas.drawMissile(startX: Float, startY: Float) {
        drawLine(
            startX,
            startY,
            startX,
            startY - missileSize,
            jetPaint
        )
    }

    private fun drawShipWings(canvas: Canvas) {
        val path = Path()

        path.moveTo(halfWidth, halfHeight - bodyTopPoint / 3) // Top

        path.lineTo(
            halfWidth - wingWidth,
            halfHeight + bodyTopPoint
        ) // Left

        path.lineTo(
            halfWidth,
            halfHeight + streamLinedTopPoint / 2
        ) // Return to mid

        path.lineTo(
            halfWidth + wingWidth,
            halfHeight + bodyTopPoint
        ) // Right

        path.close()

        canvas.drawPath(path, bodyPaint)
        canvas.drawPath(path, wingsPaintOutline)

    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                true
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.x > wingWidth && event.x < measuredWidth - wingWidth) {
                    currentShipPosition = event.x
                    displayRect.set(
                        (event.x - halfWidth).roundToInt(),
                        0,
                        (event.x + halfWidth).roundToInt(),
                        measuredHeight
                    )
                    invalidate()
                }
                true
            }
            else -> {
                super.onTouchEvent(event)
            }
        }
    }

    fun getShipY(): Float = bodyTopPoint

}