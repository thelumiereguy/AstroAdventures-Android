package com.thelumierguy.galagatest.ui.background

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.thelumierguy.galagatest.utils.CustomLifeCycleOwner
import kotlinx.coroutines.launch
import kotlin.random.Random

class StarsBackgroundView(context: Context, attributeSet: AttributeSet? = null) :
    ConstraintLayout(context, attributeSet) {


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
//        setBackgroundColor(Color.BLUE)
        setWillNotDraw(false)
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

    class Twinkles(private val height: Int, private val width: Int) {

        var xCor = Random.nextInt(0, width).toFloat()
        var yCor = Random.nextInt(0, height).toFloat()

        private val starColor by lazy {
            Color.rgb(
                Random.nextInt(0, 255),
                Random.nextInt(0, 255),
                Random.nextInt(0, 255)
            )
        }

        fun draw(canvas: Canvas, starPaint: Paint) {
            starPaint.color = starColor
            canvas.drawCircle(xCor, yCor, 3F, starPaint)

        }
    }
}
