package com.thelumierguy.astroadventures.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

class BulletStore(val maxCount: Float) {

    companion object {
        const val HALF_REFILL = 60F
        const val FULL_REFILL = 120F

        fun getAmountScore(amount: Int) = (amount * 35)
    }

    private val bulletCountFlow = MutableStateFlow(HALF_REFILL.roundToInt())

    fun bulletCountFlow(): StateFlow<Int> = bulletCountFlow

    fun updateInventory() {
        bulletCountFlow.value--
    }

    fun getAmmoCount() = bulletCountFlow.value

    fun addAmmo(ammoCount: Int) {
        bulletCountFlow.value += ammoCount
    }
}