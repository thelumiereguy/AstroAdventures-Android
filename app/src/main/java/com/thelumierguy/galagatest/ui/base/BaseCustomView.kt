package com.thelumierguy.galagatest.ui.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.thelumierguy.galagatest.utils.CustomLifeCycleOwner

open class BaseCustomView(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {

    protected val lifeCycleOwner by lazy { CustomLifeCycleOwner() }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        lifeCycleOwner.startListening()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lifeCycleOwner.stopListening()
    }
}