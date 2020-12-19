package com.thelumierguy.astroadventures.utils

import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

const val ALPHA = 0.97F

fun lowPass(
    input: FloatArray,
    output: FloatArray,
) {
    output[0] = ALPHA * output[0] + (1 - ALPHA) * input[0]
}

fun <T : Number> map(
    value: T,
    in_min: T,
    in_max: T,
    out_min: T,
    out_max: T,
): Float {
    return (value.toFloat() - in_min.toFloat()) * (out_max.toFloat() - out_min.toFloat()) / (in_max.toFloat() - in_min.toFloat()) + out_min.toFloat()
}

fun AppCompatActivity.goFullScreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        window.attributes.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    }
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

fun <T> MutableList<T>.forEachMutableSafe(operation: (T, MutableIterator<T>) -> Unit) {
    val iterator = iterator()
    while (iterator.hasNext()) {
        val item = iterator.next()
        operation(item, iterator)
    }
}

fun <T> List<T>.forEachSafe(operation: (T, Iterator<T>) -> Unit) {
    val iterator = iterator()
    while (iterator.hasNext()) {
        val item = iterator.next()
        operation(item, iterator)
    }
}