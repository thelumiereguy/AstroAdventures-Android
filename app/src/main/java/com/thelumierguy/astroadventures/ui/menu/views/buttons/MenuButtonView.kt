package com.thelumierguy.astroadventures.ui.menu.views.buttons

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import com.thelumierguy.astroadventures.R


class MenuButtonView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : AppCompatButton(context, attributeSet, defStyle) {

    private val cornerSize = 20F

    init {
        val pressedStates = ColorStateList.valueOf(
            Color.WHITE
        )
        val contentDrawable = GradientDrawable()

        contentDrawable.setColor(ResourcesCompat.getColor(context.resources,
            R.color.backgroundColorDark,
            null))

        contentDrawable.setStroke(cornerSize.toInt(),Color.BLACK)

        val rippleDrawable = RippleDrawable(pressedStates, contentDrawable, null)
        background = rippleDrawable
    }

    private val boxRect = RectF()

    private val cornerPaint = Paint().apply {
        color = ResourcesCompat.getColor(context.resources,
            R.color.backgroundColor,
            null)
        isDither = false
        isAntiAlias = false
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)



        boxRect.set(0F, 0F, cornerSize, cornerSize)
        canvas?.drawRect(boxRect, cornerPaint)

        boxRect.set(measuredWidth - cornerSize, 0F, measuredWidth.toFloat(), cornerSize)
        canvas?.drawRect(boxRect, cornerPaint)

        boxRect.set(measuredWidth - cornerSize,
            measuredHeight - cornerSize,
            measuredWidth.toFloat(),
            measuredHeight.toFloat())
        canvas?.drawRect(boxRect, cornerPaint)

        boxRect.set(0F, measuredHeight - cornerSize, cornerSize, measuredHeight.toFloat())
        canvas?.drawRect(boxRect, cornerPaint)

    }
}
