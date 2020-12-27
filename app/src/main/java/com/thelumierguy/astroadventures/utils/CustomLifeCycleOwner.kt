package com.thelumierguy.astroadventures.utils

import androidx.lifecycle.*


class CustomLifeCycleOwner : LifecycleOwner {
    private val mLifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    val customViewLifeCycleScope: LifecycleCoroutineScope
        get() = lifecycle.coroutineScope


    fun stopListening() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    fun startListening() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry
    }

    init {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }


}