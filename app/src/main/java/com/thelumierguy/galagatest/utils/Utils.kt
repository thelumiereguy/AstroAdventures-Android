package com.thelumierguy.galagatest.utils

import android.os.Build
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.Scene
import androidx.viewbinding.ViewBinding

const val ALPHA = 0.05F

fun lowPass(
    input: FloatArray,
    output: FloatArray,
) {
    output[0] = ALPHA * input[0] + output[0] * 1.0f - ALPHA
}

fun AppCompatActivity.goFullScreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.setDecorFitsSystemWindows(false)
    } else {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}