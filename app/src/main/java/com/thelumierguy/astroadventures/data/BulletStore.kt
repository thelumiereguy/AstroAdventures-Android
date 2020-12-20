package com.thelumierguy.astroadventures.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

class BulletStore(val maxCount: Float) {

    companion object {
        const val REFILL = 80F

        fun getAmountScore(amount: Int) = (amount * 35)
    }

    private val bulletCountFlow = MutableStateFlow(maxCount.roundToInt())

    fun bulletCountFlow(): StateFlow<Int> = bulletCountFlow

    fun updateInventory() {
        bulletCountFlow.value--
    }

    fun getAmmoCount() = bulletCountFlow.value

    fun addAmmo(ammoCount: Int) {
        bulletCountFlow.value += ammoCount
    }
}