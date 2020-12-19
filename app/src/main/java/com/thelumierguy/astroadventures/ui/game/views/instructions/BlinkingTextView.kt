package com.thelumierguy.astroadventures.ui.game.views.instructions

import android.content.Context
import android.util.AttributeSet
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class BlinkingTextView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : AppCompatTextView(context, attributeSet, defStyle) {


    private val blinkAnimation by lazy {
        AlphaAnimation(0.0f, 1.0f).apply {
            duration = 800
            startOffset = 20
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
    }


    fun startBlinking() {
        MainScope().launch {
            delay(500)
            isVisible = true
            startAnimation(blinkAnimation)
        }
    }

    fun stopBlinking() {
        isVisible = false
        blinkAnimation.cancel()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopBlinking()
    }
}
