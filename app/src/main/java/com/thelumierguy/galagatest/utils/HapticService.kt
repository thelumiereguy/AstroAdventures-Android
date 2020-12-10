package com.thelumierguy.galagatest.utils

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.VibrationEffect
import android.os.Vibrator


class HapticService(context: Context) {

    private var vibratorService: Vibrator? = null

    init {
        vibratorService = context.getSystemService(VIBRATOR_SERVICE) as Vibrator?
    }

    fun performHapticFeedback(time: Long, amplitude: Int = 255) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibratorService?.vibrate(VibrationEffect.createOneShot(time, amplitude))
        } else {
            vibratorService?.vibrate(time)
        }
    }
}