package com.thelumierguy.galagatest.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

class BulletStore(val maxCount: Float) {

    companion object {
        const val HALF_REFILL = 60F
    }

    private val bulletCountFlow = MutableStateFlow(HALF_REFILL.roundToInt())

    fun bulletCountFlow(): StateFlow<Int> = bulletCountFlow

    fun updateInventory() {
        bulletCountFlow.value--
    }

    fun getAmmoCount() = bulletCountFlow.value

//    fun refillInventory(resetCount: Int) {
//        bulletCountFlow.value = resetCount
//    }

}