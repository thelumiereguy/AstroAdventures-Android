package com.thelumierguy.galagatest.ui.menu.views.logo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView


class LogoTextView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : AppCompatTextView(context, attributeSet, defStyle) {


    private val borderPaint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 10F
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isDither = false
            isAntiAlias = false
            color = Color.parseColor("#E4962B")
            if (isHardwareAccelerated)
                setShadowLayer(12F, 0F, 0F, color)
        }
    }

    private val logoPathHandlerList: MutableList<LogoPathHandler> = mutableListOf()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        logoPathHandlerList.clear()
        logoPathHandlerList.add(LogoPathHandler(w.toFloat(), h.toFloat(), 0F, 0F, w.toFloat()))
        logoPathHandlerList.add(LogoPathHandler(w.toFloat(),
            h.toFloat(),
            w.toFloat(),
            h.toFloat(),
            w.toFloat()))
        if (h != 0)
            borderPaint.pathEffect = CornerPathEffect(h / 2F)
    }

    enum class Direction {
        Right,
        Down,
        Left,
        UP
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isInEditMode) {
            return
        }
        canvas?.let {
            logoPathHandlerList.forEach {
                it.startDrawingPath { path ->
                    canvas.drawPath(path, borderPaint)
                }
            }
            postInvalidate()
        }
    }


}
