package com.thelumierguy.galagatest.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

class BulletStore(val maxCount: Float,val onEmptyCallback: () -> Unit) {

    companion object {
        const val HALF_REFILL = 100F
    }

    private val bulletCountFlow = MutableStateFlow(HALF_REFILL.roundToInt())

    fun bulletCountFlow(): StateFlow<Int> = bulletCountFlow

    fun updateInventory() {
        bulletCountFlow.value--
        if (bulletCountFlow.value == 0) {
            onEmptyCallback()
        }
    }

//    fun refillInventory(resetCount: Int) {
//        bulletCountFlow.value = resetCount
//    }

}