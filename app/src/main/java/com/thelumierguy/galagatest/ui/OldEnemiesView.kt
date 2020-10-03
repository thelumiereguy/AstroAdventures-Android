package com.thelumierguy.galagatest.ui

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thelumierguy.galagatest.ui.adapter.EnemiesAdapter


class OldEnemiesView(context: Context, attributeSet: AttributeSet? = null) :
    FrameLayout(context, attributeSet) {


    private var enemiesRecycler: RecyclerView? = null

    private val enemiesList = mutableListOf<EnemiesView.Enemy>()

    init {
        initEnemies()
    }

    private fun initEnemies() {
//        repeat(30) {
//            enemiesList.add(EnemiesView.Enemy(it))
//        }
        enemiesRecycler = RecyclerView(context).apply {
            layoutManager = GridLayoutManager(context, 6)
            adapter = EnemiesAdapter(enemiesList).apply {
                notifyDataSetChanged()
            }
        }
        enemiesRecycler?.let {
            it.layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
            )
            addView(it)
        }
        startAnimationTimer()
    }

    private fun startAnimationTimer() {
        object : CountDownTimer(50000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
//                val newLocation: Float =
//                    kotlin.math.cos(millisUntilFinished.toFloat())
//
//                enemiesRecycler?.translationX = newLocation
//
//                Toast.makeText(context, "$newLocation", Toast.LENGTH_SHORT).show()
//                enemiesRecycler?.invalidate()
                enemiesRecycler?.apply {
                    animate().translationX(measuredWidth.toFloat()).apply {
                        interpolator = AccelerateDecelerateInterpolator()
                        duration = millisUntilFinished
                    }.start()
                }
            }

            override fun onFinish() {
                startAnimationTimer()
            }

        }.start()
    }




    fun checkForCollision() {

    }
}