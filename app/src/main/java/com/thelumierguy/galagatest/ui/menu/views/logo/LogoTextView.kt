package com.thelumierguy.galagatest.ui.menu.views.logo

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.abs


class LogoTextView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : AppCompatTextView(context, attributeSet, defStyle) {


    private val borderPaint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 4F
            color = Color.parseColor("#E4962B")
            pathEffect = CornerPathEffect(48F)
        }
    }

    var drawPath = Path()

    var pathLength = 0F


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        pathLength = w / 3F
    }

    var initialPointX = 0F
    var initialPointY = 0F


    enum class Direction {
        Right,
        Down,
        Left,
        UP
    }

    var segments: Int = 1


    override fun onDraw(canvas: Canvas?) {
//        if (isInEditMode) {
//            return
//        }
        super.onDraw(canvas)
        canvas?.let {
            drawPath.reset()
            drawPath.moveTo(initialPointX, initialPointY)
            startDrawingPath()
            canvas.drawPath(drawPath, borderPaint)
        }
    }

    private fun startDrawingPath() {
        drawPath(initialPointX, initialPointY, pathLength)
        translateAhead()
        postInvalidate()
    }

    private fun translateAhead() {
        when (getDirectionForPath(initialPointX, initialPointY)) {
            Direction.Right -> {
                initialPointX++
            }
            Direction.Down -> {
                initialPointY++
            }
            Direction.Left -> {
                initialPointX--
            }
            Direction.UP -> {
                initialPointY--
            }
        }
    }

    private fun drawPath(startX: Float, startY: Float, drawLength: Float) {
        segments = 1
        val direction = getDirectionForPath(startX, startY)
        val maxLength = getMaxLength(direction)
        Log.d("xy", "$startX, $startY, $drawLength, $direction")
        if (segments == 1) {
            when (direction) {
                Direction.Right -> {
                    if (startX + drawLength > maxLength) {
                        val newDrawLength = maxLength - startX
                        drawPath.lineTo(maxLength, startY)
                        segments++
                        drawPath(maxLength, startY, drawLength - newDrawLength)
                    } else {
                        drawPath.lineTo(startX + drawLength, startY)
                    }

                }
                Direction.Down -> {
                    if (startY + drawLength > maxLength) {
                        val newDrawLength = maxLength - startY
                        drawPath.lineTo(startX, maxLength)
                        segments++
                        drawPath(startX, maxLength, drawLength - newDrawLength)
                    } else {
                        drawPath.lineTo(startX, startY + drawLength)
                    }
                }
                Direction.Left -> {
                    if (startX - drawLength < maxLength) {
                        val newLength = abs(startX - drawLength)
                        drawPath.lineTo(maxLength, startY)
                        segments++
                        drawPath(maxLength, startY, newLength)
                    } else {
                        drawPath.lineTo(startX - drawLength, startY)
                    }
                }
                Direction.UP -> {
                    if (startY - drawLength < maxLength) {
                        val newLength = abs(startY - drawLength)
                        drawPath.lineTo(startX, maxLength)
                        segments++
                        drawPath(startX, maxLength, newLength)
                    } else {
                        drawPath.lineTo(startX, startY - drawLength)
                    }
                }
            }
        }
    }

    private fun getMaxLength(direction: Direction) = when (direction) {
        Direction.Right -> measuredWidth.toFloat()
        Direction.Down -> measuredHeight.toFloat()
        Direction.Left -> 0F
        Direction.UP -> 0F
    }

    private fun getDirectionForPath(startX: Float, startY: Float): Direction {
        return when {
            startX == 0F && startY == 0F -> {
                Direction.Right
            }
            startX >= measuredWidth.toFloat() && startY >= 0F && startY < measuredHeight.toFloat() -> {
                Direction.Down
            }
            startX > 0F && startX <= measuredWidth.toFloat() && startY >= measuredHeight.toFloat() -> {
                Direction.Left
            }
            startX <= 0F && startY > 0F && startY <= measuredHeight.toFloat() -> {
                Direction.UP
            }
            else -> {
                Direction.Right
            }
        }
    }
}
