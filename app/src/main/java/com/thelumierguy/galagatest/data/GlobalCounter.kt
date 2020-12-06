package com.thelumierguy.galagatest.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.receiveAsFlow

object GlobalCounter {

    private val enemyTimer = ticker(35, 1000, Dispatchers.IO)

    val enemyTimerFlow = enemyTimer.receiveAsFlow()

    private val starsBackgroundTimer = ticker(30, 1000, Dispatchers.IO)

    val starsBackgroundTimerFlow = starsBackgroundTimer.receiveAsFlow()

}
