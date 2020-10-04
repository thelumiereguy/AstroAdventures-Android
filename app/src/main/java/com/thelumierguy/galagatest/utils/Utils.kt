package com.thelumierguy.galagatest.utils

const val ALPHA = 0.05F

fun lowPass(
    input: FloatArray,
    output: FloatArray
) {
    output[0] = ALPHA * input[0] + output[0] * 1.0f - ALPHA
}
