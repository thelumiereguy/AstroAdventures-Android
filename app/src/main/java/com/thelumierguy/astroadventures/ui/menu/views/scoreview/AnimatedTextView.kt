package com.thelumierguy.astroadventures.ui.menu.views.scoreview

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.roundToInt


class AnimatedTextView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : AppCompatTextView(context, attributeSet, defStyle) {

    var initialValue: Float = 0F


    fun addNewValue(finalValue: Float, onEnd: () -> Unit = {}) {
        val valueAnimator = ValueAnimator.ofFloat(initialValue, finalValue)
        valueAnimator.duration = 1200
        valueAnimator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue
            if (value is Float)
                text = value.roundToInt().toString()
        }

        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                paint.apply {
                    setShadowLayer(12F, 0F, 0F, textColors.defaultColor)
                }
            }

            override fun onAnimationEnd(animation: Animator?) {
                paint.clearShadowLayer()
                onEnd()
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationRepeat(animation: Animator?) {
            }

        })
        valueAnimator.start()
        initialValue = finalValue
    }
}