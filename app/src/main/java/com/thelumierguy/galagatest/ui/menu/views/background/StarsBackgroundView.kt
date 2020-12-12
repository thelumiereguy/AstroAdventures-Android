package com.thelumierguy.galagatest.ui.menu.views.background

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import com.thelumierguy.galagatest.R
import com.thelumierguy.galagatest.data.GlobalCounter
import com.thelumierguy.galagatest.utils.CustomLifeCycleOwner
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.random.Random


class StarsBackgroundView(context: Context, attributeSet: AttributeSet? = null) :
    FrameLayout(context, attributeSet) {

    private var enableWarp: Boolean = false

    private val lifeCycleOwner by lazy { CustomLifeCycleOwner() }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        lifeCycleOwner.startListening()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lifeCycleOwner.stopListening()
    }

    private val starPaint by lazy {
        Paint().apply {
            isDither = false
            isAntiAlias = false
            color = ResourcesCompat.getColor(context.resources,
                R.color.starColor,
                null)
        }
    }

    private val starsList by lazy {
        List(100) {
            Twinkles(measuredHeight, measuredWidth)
        }
    }

    private val trailsList by lazy {
        List(200) {
            Trails(measuredHeight, measuredWidth)
        }
    }


    init {
        setBackgroundColor(ResourcesCompat.getColor(context.resources,
            R.color.backgroundColor,
            null))
        setWillNotDraw(false)
    }

    fun setTrails(enableWarp: Boolean) {
        this.enableWarp = enableWarp
        resetTrails()
    }

    private fun resetTrails() {
        if(!enableWarp){
            val trailsIterator = trailsList.iterator()
            while (trailsIterator.hasNext()) {
                val trails = trailsIterator.next()
                trails.reset()
            }
        }
    }

    private fun startObservingTimer() {
        GlobalCounter.starsBackgroundTimerFlow.onEach {
            if (enableWarp) {
                val trailsIterator = trailsList.iterator()
                while (trailsIterator.hasNext()) {
                    val trails = trailsIterator.next()
                    trails.translate()
                }

            } else {
                val starIterator = starsList.iterator()
                while (starIterator.hasNext()) {
                    val star = starIterator.next()
                    star.translate()
                }
            }
            invalidate()
        }.launchIn(lifeCycleOwner.customViewLifeCycleScope)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        startObservingTimer()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isInEditMode) {
            return
        }
        canvas?.let {
            if (enableWarp) {
                trailsList.forEach {
                    lifeCycleOwner.customViewLifeCycleScope.launch {
                        it.draw(canvas)
                    }
                }
            } else {
                starsList.forEach {
                    lifeCycleOwner.customViewLifeCycleScope.launch {
                        it.draw(canvas, starPaint)
                    }
                }
            }

        }
    }

    class Trails(private val height: Int, width: Int) {

        private var xCor = Random.nextInt(0, width).toFloat()
        private var yCor = Random.nextInt(0, height).toFloat()

        private var trailHeight = height * 0.05F

        private var defaultTrailHeight = height * 0.05F

        private val trailsColor by lazy {
            Color.rgb(
                Random.nextInt(0, 255),
                Random.nextInt(0, 255),
                Random.nextInt(0, 255)
            )
        }

        private val starTrailsPaint by lazy {
            Paint().apply {
                color = trailsColor
                style = Paint.Style.STROKE
                strokeWidth = 6F
                strokeCap = Paint.Cap.ROUND
            }
        }

        fun draw(canvas: Canvas) {
            canvas.drawLine(xCor, yCor, xCor, yCor + trailHeight, starTrailsPaint)
        }

        fun translate() {
            yCor += trailHeight
            slowDown()
            if (yCor > height) {
                yCor = Random.nextInt(-height, 0).toFloat()
            }
        }

        private fun slowDown() {
            if (trailHeight > 1)
                trailHeight -= 1
        }

        fun reset() {
            trailHeight = defaultTrailHeight
        }
    }

    class Twinkles(private val height: Int, width: Int) {

        private var xCor = Random.nextInt(0, width).toFloat()
        private var yCor = Random.nextInt(0, height).toFloat()
        private val radius by lazy {
            Random.nextInt(1, 7).toFloat()
        }


        private val speed by lazy {
            when {
                radius < 4F -> 0.5F
                radius == 4F -> 1F
                else -> 1.5F
            }
        }
        private val diameter by lazy {
            radius * 2F
        }

        fun draw(canvas: Canvas, starPaint: Paint) {
            starPaint.alpha = 255
            canvas.drawCircle(xCor, yCor, radius, starPaint)
            if (radius < 3) {
                starPaint.alpha = 128
                canvas.drawCircle(xCor + diameter, yCor, radius, starPaint)
                canvas.drawCircle(xCor - diameter, yCor, radius, starPaint)
                canvas.drawCircle(xCor, yCor + diameter, radius, starPaint)
                canvas.drawCircle(xCor, yCor - diameter, radius, starPaint)
            }
        }

        fun translate() {
            yCor += speed
            if (yCor > height) {
                yCor = 0F
            }
        }
    }
}
