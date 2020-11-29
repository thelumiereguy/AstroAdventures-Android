package com.thelumierguy.galagatest.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.receiveAsFlow

object GlobalCounter {

    private val timer = ticker(50, 1000, Dispatchers.IO)

    val timerFlow = timer.receiveAsFlow()

    private val startsBackgroundTimer = ticker(30, 1000, Dispatchers.IO)

    val startsBackgroundTimerFlow = startsBackgroundTimer.receiveAsFlow()

}
