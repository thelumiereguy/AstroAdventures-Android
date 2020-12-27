package com.thelumierguy.astroadventures.ui.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.thelumierguy.astroadventures.utils.CustomLifeCycleOwner

open class BaseCustomView(
    context: Context,
    attributeSet: AttributeSet? = null,
) : View(context, attributeSet) {

    protected val lifeCycleOwner by lazy { CustomLifeCycleOwner() }

    private var isActive = false
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        lifeCycleOwner.startListening()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lifeCycleOwner.stopListening()
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        isActive = isVisible
        super.onVisibilityAggregated(isVisible)
    }

    fun executeIfActive(block: () -> Unit) {
        if (isActive)
            block()
    }
}

open class BaseFrameLayout(context: Context, attributeSet: AttributeSet? = null) :
    FrameLayout(context, attributeSet) {

    protected val lifeCycleOwner by lazy { CustomLifeCycleOwner() }

    private var isActive = false
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        lifeCycleOwner.startListening()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lifeCycleOwner.stopListening()
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        isActive = isVisible
        super.onVisibilityAggregated(isVisible)
    }

    fun executeIfActive(block: () -> Unit) {
        if (isActive)
            block()
    }
}