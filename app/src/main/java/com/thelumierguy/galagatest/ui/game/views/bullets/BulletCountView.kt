package com.thelumierguy.galagatest.ui.game.views.bullets

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.thelumierguy.galagatest.R


class BulletCountView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0,
) : AppCompatTextView(context, attributeSet, defStyle) {

    fun setBulletCount(ammoCount: Int, maxCount: Float) {
        text = context.getString(R.string.ammo_reserve_text, ammoCount)
        if (ammoCount / maxCount < 0.25) {
            setTextColor(Color.RED)
        } else {
            setTextColor(Color.WHITE)
        }
    }

}
