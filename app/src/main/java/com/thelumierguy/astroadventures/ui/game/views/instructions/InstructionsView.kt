package com.thelumierguy.astroadventures.ui.game.views.instructions

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.thelumierguy.astroadventures.R


class InstructionsView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : AppCompatTextView(context, attributeSet, defStyle) {

    private val chatBubblePaint = Paint().apply {
        color = ResourcesCompat.getColor(context.resources,
            R.color.primaryFontColor,
            null)
        isAntiAlias = false
        isDither = false
    }

    private var bubbleArrowEndOffset = 0F

    private val chatBubbleRect = RectF()


    private val bubbleArrowPath = Path()

    var bubbleArrowY = 0F

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bubbleArrowEndOffset = w * 0.1F
        bubbleArrowY = measuredHeight.toFloat() - paddingBottom.toFloat()
        chatBubbleRect.set(0F,
            0F,
            measuredWidth.toFloat() - 0F,
            bubbleArrowY
        )
    }

    private var valueAnimator: ValueAnimator? = null

    private var currentTextLength = 0

    fun addDialog(text: String) {
        currentTextLength = 0
        isVisible = true
        valueAnimator?.cancel()

        valueAnimator = ValueAnimator.ofInt(currentTextLength, text.length)
            .setDuration(500L).apply {
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener {
                    val currentProgress = it.animatedValue
                    if (currentProgress is Int) {
                        val subText = text.substring(0, currentProgress)
                        setText(subText)
                    }
                }
                start()
            }
    }

    override fun onDraw(canvas: Canvas?) {
        drawBubbleArrow()
        canvas?.drawPath(
            bubbleArrowPath,
            chatBubblePaint)
        canvas?.drawRoundRect(chatBubbleRect,
            measuredHeight * 0.2F,
            measuredHeight * 0.2F,
            chatBubblePaint)
        super.onDraw(canvas)
    }

    private fun drawBubbleArrow() {
        bubbleArrowPath.reset()
        bubbleArrowPath.apply {
            moveTo(measuredWidth - bubbleArrowEndOffset, bubbleArrowY)
            lineTo(measuredWidth - bubbleArrowEndOffset,
                measuredHeight.toFloat())

            lineTo(measuredWidth - paddingBottom - bubbleArrowEndOffset,
                bubbleArrowY)
            close()
        }
    }
}
