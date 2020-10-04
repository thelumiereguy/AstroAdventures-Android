package com.thelumierguy.galagatest.ui.playership

import android.content.Context
import android.graphics.*
import android.graphics.drawable.PictureDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import com.thelumierguy.galagatest.ui.base.BaseCustomView
import kotlin.math.roundToInt


class P38LightningView(context: Context, attributeSet: AttributeSet? = null) :
    BaseCustomView(context, attributeSet) {

    private var currentShipPosition: Float = 0F

    private val rudderPaint = Paint().apply {
        color = Color.parseColor("#7C8586")
        isAntiAlias = false
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
        isDither = false
    }

    private val bodyPaint = Paint().apply {
        color = Color.parseColor("#707146")
        isAntiAlias = false
        strokeJoin = Paint.Join.ROUND
        style = Paint.Style.FILL_AND_STROKE
        isDither = false
    }

    private val cockPitPaint = Paint().apply {
        color = Color.parseColor("#0F100D")
        isAntiAlias = false
        isDither = false
    }


    private val wingsPaint = Paint().apply {
        color = Color.parseColor("#7C8586")
        isAntiAlias = false
        strokeJoin = Paint.Join.ROUND
        style = Paint.Style.FILL_AND_STROKE
        isDither = false
    }

    private val engineTip = Paint().apply {
        color = Color.parseColor("#842D21")
        isAntiAlias = false
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

        val canvas = spaceShipPicture.beginRecording(measuredWidth, measuredHeight)
        canvas.let {
            drawStreamlinedBody(it)
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
        streamLinedTopPoint = h * 0.1F
        bodyTopPoint = h * 0.30F
        wingWidth = w / 15F
        missileSize = h / 8F
        initPicture()
    }

    private fun drawStreamlinedBody(it: Canvas) {
        val path = Path()

        val thirtyFromHalfWidthLeft = halfWidth - (halfWidth * 0.02F)
        val twentyFromHalfWidthLeft = halfWidth - (halfWidth * 0.03F)

        val thirtyFromHalfWidthRight = halfWidth + (halfWidth * 0.02F)
        val twentyFromHalfWidthRight = halfWidth + (halfWidth * 0.03F)

        path.apply {
            moveTo(thirtyFromHalfWidthLeft, streamLinedTopPoint)
            lineTo(halfWidth, 0F)
            lineTo(thirtyFromHalfWidthRight, streamLinedTopPoint)
            close()
        }

        it.drawPath(
            path,
            cockPitPaint
        )
        bodyPaint.style = Paint.Style.FILL


        val path2 = Path()

        val thirtyFromHalfHeightBottom = halfHeight + (halfHeight * 0.03F)

        path2.apply {
            moveTo(thirtyFromHalfWidthLeft, streamLinedTopPoint)
            lineTo(twentyFromHalfWidthLeft, bodyTopPoint)
            lineTo(twentyFromHalfWidthRight, bodyTopPoint)
            lineTo(thirtyFromHalfWidthRight, streamLinedTopPoint)
            close()

        }

        val path4 = Path()

        val quarterWidth = halfWidth - (halfWidth * 0.1F)
        val quarterWidthRight = halfWidth + (halfWidth * 0.1F)
        val tenFromHalfHeight = halfHeight - (halfHeight * 0.1F)

        path4.apply {
            moveTo(halfWidth, bodyTopPoint)
            lineTo(quarterWidth, bodyTopPoint)
            lineTo(quarterWidth, tenFromHalfHeight)
            lineTo(halfWidth, halfHeight)
            lineTo(quarterWidthRight, tenFromHalfHeight)
            lineTo(quarterWidthRight, bodyTopPoint)
            close()
        }

        it.drawPath(
            path4,
            wingsPaint
        )


        it.drawPath(
            path2,
            bodyPaint
        )

        val path3 = Path()

        path3.apply {
            moveTo(twentyFromHalfWidthLeft, bodyTopPoint)
            lineTo(halfWidth, thirtyFromHalfHeightBottom)
            lineTo(twentyFromHalfWidthRight, bodyTopPoint)
            close()

        }

        it.drawPath(
            path3,
            cockPitPaint
        )

        val path5 = Path()

        val engineRadius = 15F
        val leftEngineTopRight = quarterWidth - (engineRadius / 2)
        val leftEngineTopLeft = leftEngineTopRight - (engineRadius / 2)
        val leftEngineBottomLeft = leftEngineTopLeft - (engineRadius / 2)
        val engineHeight = streamLinedTopPoint * 2

        path5.apply {
            moveTo(quarterWidth, bodyTopPoint)
            lineTo(leftEngineTopRight, engineHeight)
            lineTo(leftEngineTopLeft, engineHeight)
            lineTo(leftEngineBottomLeft, bodyTopPoint)
            close()
        }

        it.drawPath(
            path5,
            bodyPaint
        )

        val path6 = Path()
        val rightEngineTopLeft = quarterWidthRight + (engineRadius / 2)
        val rightEngineTopRight = rightEngineTopLeft + (engineRadius / 2)
        val rightEngineBottomLeft = rightEngineTopRight + (engineRadius / 2)

        path6.apply {
            moveTo(quarterWidthRight, bodyTopPoint)
            lineTo(rightEngineTopLeft, engineHeight)
            lineTo(rightEngineTopRight, engineHeight)
            lineTo(rightEngineBottomLeft, bodyTopPoint)
            close()
        }

        it.drawPath(
            path6,
            bodyPaint
        )

        //LEft engine tip
        val path7 = Path()

        val tipHeight = streamLinedTopPoint + (streamLinedTopPoint / 2)
        val leftTipPoint = leftEngineTopLeft + (leftEngineTopRight - leftEngineTopLeft) / 2
        path7.apply {
            moveTo(leftEngineTopRight, engineHeight)
            lineTo(leftTipPoint, tipHeight)
            lineTo(leftEngineTopLeft, engineHeight)
            close()
        }

        it.drawPath(
            path7,
            engineTip
        )

        //Right engine tip
        val path8 = Path()


        val rightTipPoint = rightEngineTopLeft + (rightEngineTopRight - rightEngineTopLeft) / 2

        path8.apply {
            moveTo(rightEngineTopLeft, engineHeight)
            lineTo(rightTipPoint, tipHeight)
            lineTo(rightEngineTopRight, engineHeight)
            close()
        }

        it.drawPath(
            path8,
            engineTip
        )

        //left engine Body
        val path9 = Path()


        path9.apply {
            moveTo(quarterWidth, bodyTopPoint)
            lineTo(quarterWidth, tenFromHalfHeight)
            lineTo(leftEngineBottomLeft, tenFromHalfHeight)
            lineTo(leftEngineBottomLeft, bodyTopPoint)
            close()
        }

        it.drawPath(
            path9,
            cockPitPaint
        )

        //right engine Body
        val path10 = Path()


        path10.apply {
            moveTo(quarterWidthRight, bodyTopPoint)
            lineTo(quarterWidthRight, tenFromHalfHeight)
            lineTo(rightEngineBottomLeft, tenFromHalfHeight)
            lineTo(rightEngineBottomLeft, bodyTopPoint)
            close()
        }

        it.drawPath(
            path10,
            cockPitPaint
        )

        //Left Wing
        val path11 = Path()
        val wingHeight = bodyTopPoint / 4
        val wingTop = bodyTopPoint + 10

        val wingBottom = wingTop + wingHeight

        val extremeWingWidth = (halfWidth / 3.5F)

        path11.apply {
            moveTo(leftEngineBottomLeft, bodyTopPoint)
            lineTo(halfWidth - extremeWingWidth, wingTop)
            lineTo(halfWidth - extremeWingWidth, wingBottom)
            lineTo(leftEngineBottomLeft, tenFromHalfHeight)
            close()
        }

        it.drawPath(
            path11,
            wingsPaint
        )


        //Right Wing
        val path12 = Path()

        path12.apply {
            moveTo(rightEngineBottomLeft, bodyTopPoint)
            lineTo(halfWidth + extremeWingWidth, wingTop)
            lineTo(halfWidth + extremeWingWidth, wingBottom)
            lineTo(rightEngineBottomLeft, tenFromHalfHeight)
            close()
        }

        it.drawPath(
            path12,
            wingsPaint
        )


        //left engine tail
        val path13 = Path()

        val engineTailHeight = measuredHeight - (measuredHeight / 10F)

        val leftEngineTailTip = quarterWidth - (quarterWidth - leftEngineBottomLeft) / 2

        path13.apply {
            moveTo(quarterWidth, tenFromHalfHeight)
            lineTo(leftEngineBottomLeft, tenFromHalfHeight)
            lineTo(leftEngineTailTip, engineTailHeight)
            close()
        }


        //right engine tail
        val path14 = Path()

        val rightEngineTailTip = quarterWidthRight + (quarterWidth - leftEngineBottomLeft) / 2

        path14.apply {
            moveTo(quarterWidthRight, tenFromHalfHeight)
            lineTo(rightEngineBottomLeft, tenFromHalfHeight)
            lineTo(rightEngineTailTip, engineTailHeight)
            close()
        }


        //rudder
        val rudderOffset = halfWidth * 0.01F
        rudderPaint.strokeWidth = engineRadius

        val rudderPosition = engineTailHeight - engineRadius

        it.drawLine(
            leftEngineTailTip - rudderOffset,
            rudderPosition,
            rightEngineTailTip + rudderOffset,
            rudderPosition,
            rudderPaint
        )

        it.drawPath(
            path13,
            bodyPaint
        )

        it.drawPath(
            path14,
            bodyPaint
        )

    }

    fun getShipX() = currentShipPosition


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