package com.thelumierguy.galagatest.ui.game.views.playerhealth

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.thelumierguy.galagatest.data.PlayerHealthInfo
import com.thelumierguy.galagatest.data.PlayerHealthInfo.MAX_HEALTH
import com.thelumierguy.galagatest.ui.base.BaseCustomView
import com.thelumierguy.galagatest.utils.map
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


class PlayerHealthView constructor(context: Context, attributeSet: AttributeSet? = null) :
    BaseCustomView(context, attributeSet) {


    private val colorOne by lazy {
        Paint().apply {
            color = Color.parseColor("#DD3D1F")
        }
    }

    private val colorTwo by lazy {
        Paint().apply {
            color = Color.parseColor("#EA4C46")
        }
    }

    private val colorThree by lazy {
        Paint().apply {
            color = Color.parseColor("#F07470")
        }
    }

    private val colorFour by lazy {
        Paint().apply {
            color = Color.parseColor("#F1959B")
        }
    }

    private val colorFive by lazy {
        Paint().apply {
            color = Color.parseColor("#F6BDC0")
        }
    }

    var onHealthEmpty: (() -> Unit)? = null


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startObservingHealth()
    }

    private fun startObservingHealth() {
        lifeCycleOwner.customViewLifeCycleScope.launch {
            PlayerHealthInfo.getPlayerHealthFlow().collect {
                launch {
                    handleVisibility(it)
                }
            }
        }
    }

    private fun handleVisibility(it: Int) {
        if (it != MAX_HEALTH) {
            if (it != 0) {
                isVisible = true
                numberOfBars =
                    map(it,
                        MAX_HEALTH,
                        0,
                        0,
                        healthBarPaintList.size).roundToInt()
                if (numberOfBars >= healthBarPaintList.size) {
                    numberOfBars = healthBarPaintList.size - 1
                }
            } else {
                onHealthEmpty?.invoke()
            }
            invalidate()
        } else {
            isInvisible = true
        }
    }

    private var numberOfBars = 0

    private val healthBarPaintList = listOf(colorOne, colorTwo, colorThree, colorFour, colorFive)

    var blinkingJob: Job = Job()

    private fun startFlowing() {
        blinkingJob.cancelChildren()
        blinkingJob = lifeCycleOwner.customViewLifeCycleScope.launch {
            if (isVisible) {
                ticker(600).receiveAsFlow().collect {
                    invalidate()
                }
            }
        }
    }

    var indicatorHeight = 0F

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        indicatorHeight = h * 0.005F
    }

    override fun onDraw(canvas: Canvas?) {
        Log.d("Bars", "$numberOfBars")
        (0 until numberOfBars).forEach {
            val yEnd = it * indicatorHeight
            val yStart = (it - 1) * indicatorHeight
            healthBarPaintList[it].alpha = 255 - (it * 5)
            canvas?.drawRect(0F,
                measuredHeight - yEnd,
                measuredWidth.toFloat(),
                measuredHeight - yStart,
                healthBarPaintList[it])
        }
    }

}