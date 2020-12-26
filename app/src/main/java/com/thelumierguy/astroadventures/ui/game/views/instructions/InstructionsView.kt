package com.thelumierguy.astroadventures.ui.game.views.instructions

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.thelumierguy.astroadventures.R
import com.thelumierguy.astroadventures.utils.scaleView
import com.thelumierguy.astroadventures.utils.scaleToOriginal


class InstructionsView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : AppCompatTextView(context, attributeSet, defStyle) {


    init {
        setOnTouchListener(CustomOnTouchListenerImpl(-0.2F))
    }

    private val borderThickness = 10F

    private val chatBubblePaint = Paint().apply {
        color = ResourcesCompat.getColor(context.resources,
            R.color.primaryFontColor,
            null)
        isAntiAlias = false
        isDither = false
    }

    private val borderPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = borderThickness
        strokeJoin = Paint.Join.BEVEL
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
        chatBubbleRect.set(borderThickness,
            borderThickness,
            measuredWidth.toFloat() - borderThickness,
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
            .setDuration(1000L).apply {
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
        canvas?.drawRect(chatBubbleRect,
            chatBubblePaint)
        drawBorder()
        canvas?.drawPath(
            bubbleArrowPath,
            borderPaint)
        super.onDraw(canvas)
    }


    private fun drawBorder() {
        bubbleArrowPath.reset()
        bubbleArrowPath.apply {
            moveTo(0F, 0F)
            lineTo(0F, bubbleArrowY)
            lineTo(measuredWidth - paddingBottom - bubbleArrowEndOffset, bubbleArrowY)
            lineTo(measuredWidth - bubbleArrowEndOffset,
                measuredHeight.toFloat())
            lineTo(measuredWidth - bubbleArrowEndOffset, bubbleArrowY)
            lineTo(measuredWidth.toFloat(), bubbleArrowY)
            lineTo(measuredWidth.toFloat(), 0F)
            close()
        }
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


class CustomOnTouchListenerImpl constructor(private val scaleByVal: Float) : View.OnTouchListener {


    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                v.scaleView(scaleByVal)
                return true
            }
            MotionEvent.ACTION_UP -> {
                v.scaleToOriginal()
                v.performClick()
                return true
            }
        }
        return false
    }
}
