package com.thelumierguy.astroadventures.utils

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator

fun View.scaleView(scaleByVal: Float) {
    animate()
        .scaleXBy(scaleByVal)
        .scaleYBy(scaleByVal)
        .setDuration(100)
        .withEndAction {
            Handler(Looper.getMainLooper()).postDelayed({
                scaleToOriginal()
            }, 200)
        }
        .setInterpolator(LinearInterpolator())
        .start()
}

fun View.scaleToOriginal() {
    animate().cancel()
    animate()
        .scaleX(1f)
        .scaleY(1f)
        .setDuration(500)
        .setInterpolator(OvershootInterpolator())
        .start()

}