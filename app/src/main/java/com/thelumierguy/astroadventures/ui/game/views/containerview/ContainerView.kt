package com.thelumierguy.astroadventures.ui.game.views.containerview

import android.content.Context
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.random.Random


class ContainerView(context: Context, attributeSet: AttributeSet? = null) :
    ConstraintLayout(context, attributeSet) {

    fun startShakeAnimation(duration: Int, offset: Int, repeatCount: Int) {
        val newOffsetFromX = Random.nextInt(-1, 1).toFloat() * offset
        val newOffsetToX = Random.nextInt(-1, 1).toFloat() * offset
        val newOffsetFromY = Random.nextInt(-1, 1).toFloat() * offset
        val newOffsetToY = Random.nextInt(-1, 1).toFloat() * offset
        val anim: Animation = TranslateAnimation(
            newOffsetFromX,
            newOffsetToX,
            newOffsetFromY,
            newOffsetToY
        )
        anim.repeatCount = 1
        anim.repeatMode = Animation.REVERSE
        anim.duration = duration.toLong()
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                val newRepeatCount = repeatCount.dec()
                if (newRepeatCount > 0) {
                    startShakeAnimation(duration, offset, newRepeatCount)
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })
        startAnimation(anim)
    }

}
