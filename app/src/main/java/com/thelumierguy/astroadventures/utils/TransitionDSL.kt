package com.thelumierguy.astroadventures.utils

import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionSet

inline fun transitionSet(block: TransitionSet.() -> Unit): TransitionSet {
    return TransitionSet().apply(block)
}

inline fun TransitionSet.addSet(block: TransitionSet.() -> Unit) {
    addTransition(TransitionSet().apply(block))
}

inline fun TransitionSet.addTransition(transition: Transition, block: Transition.() -> Unit) {
    transition.apply(block)
    addTransition(transition)
}

inline fun Transition.attachListener(crossinline onStart: () -> Unit, noinline onEnd: ((Transition) -> Unit)? = null) {

    addListener(object : TransitionListenerAdapter() {
        override fun onTransitionStart(transition: Transition) {
            onStart()
        }

        override fun onTransitionEnd(transition: Transition) {
            onEnd?.let { it(transition) }
        }
    })
}

inline fun Transition.onStart(crossinline onStart: () -> Unit) {

    addListener(object : TransitionListenerAdapter() {
        override fun onTransitionStart(transition: Transition) {
            onStart()
        }
    })
}

inline fun Transition.onEnd(crossinline onEnd: () -> Unit) {

    addListener(object : TransitionListenerAdapter() {
        override fun onTransitionEnd(transition: Transition) {
            onEnd()
        }
    })
}