package com.thelumierguy.galagatest.ui.enemyShip

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.random.Random


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

    var onCollisionDetector: OnCollisionDetector? = null


    private val enemyList = mutableListOf(
        mutableMapOf<EnemyLocationRange, List<Enemy?>>()
    )


    private val bulletPositionList: MutableMap<MutableMap<EnemyLocationRange, List<Enemy?>>, MutableStateFlow<Pair<Float, Float>>> =
        mutableMapOf()


    private fun initEnemies() {
        enemyList.clear()
        repeat(4) { x ->

            val enemiesList = MutableList(4) { y ->
                Enemy.builder(measuredWidth, x, y)
            }

            val range = enemiesList.getRangeX()

            enemyList.add(
                mutableMapOf(
                    EnemyLocationRange(
                        range.first, range.second
                    ) to enemiesList
                )
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initEnemies()
    }


    override fun onDraw(canvas: Canvas?) {
        enemyList.flatMap {
            it.values.flatten()
        }.forEach {
            it?.onDraw(canvas)
        }
    }

    fun checkCollision(bulletPosition: MutableStateFlow<Pair<Float, Float>>) {
        lifeCycleOwner.customViewLifeCycleScope.launch {
            val bulletRange =
                enemyList
                    .flatMap {
                        it.keys.toList()
                    }.find {
                        it.isBetween(bulletPosition.value.first)
                    }


            val enemySet = enemyList.find {
                it.containsKey(bulletRange)
            }
            enemySet?.values
            val enemyInLine = enemySet?.values?.reversed()

            enemyInLine?.let {
                bulletPositionList[enemySet] = bulletPosition
                startObservingBulletPosition()
            }
        }
    }

    private fun startObservingBulletPosition() {
        bulletPositionList.forEach { bulletPositionMap ->
            lifeCycleOwner.customViewLifeCycleScope.launch {
                bulletPositionMap.value.collect { bulletPosition ->
                    Log.d("Bullet before", " $bulletPosition")
                    val key = bulletPositionMap.key.keys
                    val enemyInList = enemyList.find { it.keys == key }?.values

                    if (!enemyInList.isNullOrEmpty()) {
                        val enemyInLine = enemyInList.flatten().reversed().find {
                            it?.checkEnemyYPosition(bulletPosition.second) ?: false
                        }

                        enemyInLine?.let {
                            Log.d("Bullet", "${it.enemyX} ${it.enemyY} $bulletPosition")
                            destroyBullet(bulletPositionMap.value)
                            destroyEnemy(it)
                        }
                    }
                }
            }
        }
    }

    private fun destroyBullet(bulletPositionMap: MutableStateFlow<Pair<Float, Float>>) {
        bulletPositionList.onEachIndexed { index, entry ->
            if (bulletPositionMap == entry.value) {
                onCollisionDetector?.onCollision(index)
                return@onEachIndexed
            }
        }
    }

    private fun destroyEnemy(enemyInLine: Enemy) {
        enemyList.flatMap { it.values }.flatten().forEach {
            if (it == enemyInLine) {
                it.isVisible = false
            }
        }
        postInvalidate()
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

    class Enemy(val radius: Float) {

        private val drawRect = RectF(
            0F, 0F, 0F, 0F
        )

        var enemyY = 0F
        var enemyX = 0F

        companion object {
            fun builder(width: Int, positionX: Int, positionY: Int): Enemy {
                return Enemy(width / 20F).apply {
                    drawRect.set(
                        (width / 6F) * positionX,
                        (width / 4F) * positionY,
                        (width / 6F) * (positionX + 1),
                        (width / 4F) * (positionY + 1),
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

        var isVisible: Boolean = true


        fun onDraw(canvas: Canvas?) {
            if (isVisible)
                canvas?.drawCircle(enemyX, enemyY, radius, paint)
        }

        fun isInRange(bulletX: Float): Boolean {
            return Range(enemyX - radius, enemyX + radius).contains(bulletX)
        }

        fun checkEnemyYPosition(bulletY: Float): Boolean {
            return Range(enemyY - radius, enemyY + radius).contains(bulletY) && isVisible
        }
    }

    private fun List<Enemy>.getRangeX(): Pair<Float, Float> {
        return if (size > 0) {
            val enemy = get(0)
            Pair(enemy.enemyX - enemy.radius, enemy.enemyX + enemy.radius)
        } else {
            Pair(0F, 0F)
        }
    }
}

interface OnCollisionDetector {
    fun onCollision(index: Int)
}

typealias EnemyLocationRange = Range<Float>

fun EnemyLocationRange.isBetween(value: Float): Boolean {
    return contains(value)
}


data class EnemyColumn(val range: EnemyLocationRange, val enemyList: MutableList<EnemiesView.Enemy>)
