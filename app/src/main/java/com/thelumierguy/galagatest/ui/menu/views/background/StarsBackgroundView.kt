package com.thelumierguy.galagatest.ui.menu.views.background

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.thelumierguy.galagatest.utils.CustomLifeCycleOwner
import com.thelumierguy.galagatest.data.GlobalCounter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.random.Random

class StarsBackgroundView(context: Context, attributeSet: AttributeSet? = null) :
    FrameLayout(context, attributeSet) {


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
        Paint()
    }

    private val starsList by lazy {
        List(100) {
            Twinkles(measuredHeight, measuredWidth)
        }
    }

    private var enableTinkling = true
        set(value) {
            field = value
            invalidate()
        }


    init {
        setWillNotDraw(false)
    }

    private fun startObservingTimer() {
        GlobalCounter.startsBackgroundTimerFlow.onEach {
            val starIterator = starsList.iterator()
            while (starIterator.hasNext()) {
                val star = starIterator.next()
                star.translate()
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
        if (isInEditMode || !enableTinkling) {
            return
        }
        canvas?.let {
            starsList.forEach {
                lifeCycleOwner.customViewLifeCycleScope.launch {
                    it.draw(canvas, starPaint)
                }
            }
        }
    }

    class Twinkles(private val height: Int, width: Int) {

        var xCor = Random.nextInt(0, width).toFloat()
        var yCor = Random.nextInt(0, height).toFloat()
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

        private val starColor by lazy {
            Color.LTGRAY
        }

        fun draw(canvas: Canvas, starPaint: Paint) {
            starPaint.color = starColor
            canvas.drawCircle(xCor, yCor, radius, starPaint)

        }

        fun translate() {
            yCor += speed
            if (yCor > height) {
                yCor = 0F
            }
        }
    }
}
