package com.thelumierguy.galagatest.ui.game.views.playerhealth

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.res.ResourcesCompat
import com.thelumierguy.galagatest.R
import com.thelumierguy.galagatest.data.PlayerHealthInfo
import com.thelumierguy.galagatest.data.PlayerHealthInfo.MAX_HEALTH
import com.thelumierguy.galagatest.ui.base.BaseCustomView
import com.thelumierguy.galagatest.utils.map
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


class PlayerHealthView constructor(context: Context, attributeSet: AttributeSet? = null) :
    BaseCustomView(context, attributeSet) {


    private val heartPaint by lazy {
        Paint().apply {
            color = Color.parseColor("#DD3D1F")
        }
    }


    private val circlePaint by lazy {
        Paint().apply {
            color = ResourcesCompat.getColor(context.resources, R.color.shipShadowColor, null)
            strokeWidth = 2F
            style = Paint.Style.STROKE
        }
    }

    private val healthProgress by lazy {
        Paint().apply {
            color = Color.parseColor("#DD3D1F")
            style = Paint.Style.STROKE
            strokeWidth = measuredHeight / 4F
            if (isHardwareAccelerated)
                setShadowLayer(12F, 0F, 0F, color)
        }
    }

    var onHealthEmpty: (() -> Unit)? = null


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode)
            startObservingHealth()
    }

    var progressLength = 0F

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        progressLength = map(PlayerHealthInfo.getPlayerHealthValue(),
            0,
            MAX_HEALTH,
            measuredHeight,
            measuredWidth)
    }

    private var valueAnimator: ValueAnimator? = null


    private fun startObservingHealth() {
        lifeCycleOwner.customViewLifeCycleScope.launch {
            PlayerHealthInfo.getPlayerHealthFlow().collect { life ->
                launch {
                    if (life <= 0) {
                        onHealthEmpty?.invoke()
                    }
                    val progress = map(life, 0, MAX_HEALTH, measuredHeight, measuredWidth)
                    animateProgress(progress)
                }
            }
        }
    }

    private fun animateProgress(progress: Float) {
        if (progressLength != progress) {
            valueAnimator?.cancel()

            valueAnimator = ValueAnimator.ofFloat(progressLength, progress)
                .setDuration(500L).apply {
                    interpolator = AccelerateDecelerateInterpolator()
                    addUpdateListener {
                        val currentProgress = it.animatedValue
                        if (currentProgress is Float) {
                            progressLength = currentProgress
                            postInvalidate()
                        }
                    }
                    start()
                }
        }
    }


    override fun onDraw(canvas: Canvas?) {
        val radius = measuredHeight / 2F
        canvas?.drawCircle(radius + paddingLeft, radius + paddingTop, radius, circlePaint)
        canvas?.drawLine(measuredHeight.toFloat(), radius, progressLength, radius, healthProgress)
        val path = createHeartPath(2 * (radius.roundToInt() + paddingLeft), measuredHeight)
        canvas?.drawPath(path, heartPaint)
    }

    private fun createHeartPath(width: Int, height: Int): Path {
        val path = Path()
        val bottomPointX = width / 2F
        val bottomPointY = 0.9F * height
        val midPointLength = 0.4 * height
        val topSidePointLength = 0.7 * height

        val controlPointHeight = midPointLength * 0.6

        //start point
        path.moveTo(bottomPointX, bottomPointY)

        var angle = 225.0

        //left mid point
        val midPointLeftX = bottomPointX + midPointLength * cos(Math.toRadians(angle))
        val midPointLeftY = bottomPointY + midPointLength * sin(Math.toRadians(angle))
        path.lineTo(midPointLeftX.toFloat(), midPointLeftY.toFloat())

        angle = 220.0

        //control point left
        val controlPointLeftX =
            midPointLeftX + controlPointHeight * cos(Math.toRadians(angle))
        val controlPointLeftY =
            midPointLeftY + controlPointHeight * sin(Math.toRadians(angle))

        angle = 235.0

        //top left point
        val topLeftPointX = bottomPointX + topSidePointLength * cos(Math.toRadians(angle))
        val topLeftPointY = bottomPointY + topSidePointLength * sin(Math.toRadians(angle))

        path.quadTo(
            controlPointLeftX.toFloat(),
            controlPointLeftY.toFloat(),
            topLeftPointX.toFloat(),
            topLeftPointY.toFloat()
        )

        //top control point left

        val offsetXControlPoint = width * 0.2F
        val offsetYControlPoint = 0F

        val controlPointTopX = width / 2 - offsetXControlPoint


        //mid point top
        val midTopX = width / 2F
        val midTopY = height * 0.3F
        path.quadTo(
            controlPointTopX,
            offsetYControlPoint,
            midTopX,
            midTopY
        )
        //back to start
        path.lineTo(bottomPointX, bottomPointY)


        angle = 315.0

        //right mid point
        val midPointRightX = bottomPointX + midPointLength * cos(Math.toRadians(angle))
        val midPointRightY = bottomPointY + midPointLength * sin(Math.toRadians(angle))
        path.lineTo(midPointRightX.toFloat(), midPointRightY.toFloat())

        angle = 320.0

        //control point right
        val controlPointRightX =
            midPointRightX + controlPointHeight * cos(Math.toRadians(angle))
        val controlPointRightY =
            midPointRightY + controlPointHeight * sin(Math.toRadians(angle))

        angle = 305.0

        //top right point
        val topRightPointX = bottomPointX + topSidePointLength * cos(Math.toRadians(angle))
        val topRightPointY = bottomPointY + topSidePointLength * sin(Math.toRadians(angle))

        path.quadTo(
            controlPointRightX.toFloat(),
            controlPointRightY.toFloat(),
            topRightPointX.toFloat(),
            topRightPointY.toFloat()
        )

        //top control point right

        val controlPointTopXRight = width / 2 + offsetXControlPoint


        path.quadTo(
            controlPointTopXRight,
            offsetYControlPoint,
            midTopX,
            midTopY
        )

        return path
    }
}