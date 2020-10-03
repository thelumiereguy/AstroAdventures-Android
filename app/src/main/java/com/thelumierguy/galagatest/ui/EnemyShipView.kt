package com.thelumierguy.galagatest.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View


class EnemyShipView(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {

    private val bodyPaint = Paint().apply {
        color = Color.parseColor("#379797")
        isAntiAlias = false
        strokeWidth = 8F
        isDither = false
    }

    val radius = 30F


    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            it.drawCircle(measuredWidth / 2F, measuredHeight / 2F, radius, bodyPaint)
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(100, 100)
    }
}