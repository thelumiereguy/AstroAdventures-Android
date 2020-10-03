package com.thelumierguy.galagatest.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.util.Range
import android.view.View
import com.thelumierguy.galagatest.utils.CustomLifeCycleOwner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.random.Random


@ExperimentalCoroutinesApi
class EnemiesView(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {

    private val lifeCycleOwner by lazy { CustomLifeCycleOwner() }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        lifeCycleOwner.startListening()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lifeCycleOwner.stopListening()
    }

    private val enemiesList = mutableListOf(mutableListOf<Enemy?>())
    private val bulletPositionList =
        mutableMapOf<Int, MutableStateFlow<Pair<Float, Float>>>()

    private fun initEnemies() {
        enemiesList.clear()
        repeat(4) { x ->
            enemiesList.add(MutableList(4) { y ->
                Enemy.builder(measuredWidth, x, y)
            })
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initEnemies()
    }


    override fun onDraw(canvas: Canvas?) {
        enemiesList.forEach {
            it.forEach {
                it?.onDraw(canvas)
            }
        }
    }

    fun checkCollision(bulletPosition: MutableStateFlow<Pair<Float, Float>>) {
        val enemyInLine = enemiesList.flatten().find {
            it?.isInRange(bulletPosition.value.first) ?: false
        }

        enemyInLine?.let { enemyInLineNonNull ->
            val position = enemiesList.flatten().indexOf(enemyInLineNonNull)
            bulletPositionList[position] = bulletPosition
            startObservingBulletPosition(bulletPosition)
        }
    }

    private fun startObservingBulletPosition(bulletPosition: MutableStateFlow<Pair<Float, Float>>) {
        lifeCycleOwner.customViewLifeCycleScope.launch {
            bulletPositionList.forEach {
                bulletPosition.collect { bulletPosition ->
                    Log.d("Bullet Y","$bulletPosition")
                    val enemyInLine = enemiesList.flatten()[it.key]
                    enemyInLine?.let {
                        if (it.checkEnemyYPosition(bulletPosition.second)) {
                            destroyEnemy(it)
                        }
                    }
                }
            }
        }
    }

    private fun destroyEnemy(enemyInLine: Enemy) {
        enemiesList.forEach { list ->
            val iterator = list.iterator()
            if (iterator.hasNext()) {
                val enemy = iterator.next()
                if (enemy == enemyInLine) {
                    iterator.remove()
                }
            }
        }
    }

    fun removeBullet(bullet: MutableStateFlow<Pair<Float, Float>>) {
        val iterator = bulletPositionList.iterator()
        if (iterator.hasNext()) {
            val enemy = iterator.next()
            if (enemy == bullet) {
                iterator.remove()
            }
        }
    }

    class Enemy(private var radius: Float) {

        private val drawRect = RectF(
            0F, 0F, 0F, 0F
        )

        var enemyY = 0F
        var enemyX = 0F

        companion object {
            fun builder(width: Int, positionX: Int, positionY: Int): Enemy {
                return Enemy(width / 20F).apply {
                    drawRect.set(
                        width / 6F * positionX,
                        width / 4F * positionY,
                        width / 6F * (positionX + 1),
                        width / 4F * (positionY + 1),
                    )
                    enemyX = drawRect.centerX()
                    enemyY = drawRect.centerY()
                }
            }
        }

        private val paint by lazy {
            Paint().apply {
                color = Color.rgb(
                    Random.nextInt(128, 255),
                    Random.nextInt(128, 255),
                    Random.nextInt(128, 255)
                )
                isAntiAlias = false
                strokeWidth = 8F
                isDither = false
            }
        }

        val isVisible: Boolean = true


        fun onDraw(canvas: Canvas?) {
            canvas?.drawCircle(enemyX, enemyY, radius, paint)
        }

        fun isInRange(bulletX: Float): Boolean {
            return Range(enemyX - radius, enemyX + radius).contains(bulletX)
        }

        fun checkEnemyYPosition(bulletY: Float): Boolean {
            return Range(enemyY - radius, enemyY + radius).contains(bulletY)
        }
    }
}
