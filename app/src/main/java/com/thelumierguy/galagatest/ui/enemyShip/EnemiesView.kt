package com.thelumierguy.galagatest.ui.enemyShip

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.util.Range
import com.thelumierguy.galagatest.ui.base.BaseCustomView
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.random.Random


class EnemiesView(context: Context, attributeSet: AttributeSet? = null) :
    BaseCustomView(context, attributeSet) {

    var onCollisionDetector: OnCollisionDetector? = null


    private val enemyList = mutableListOf(
        EnemyColumn()
    )

    private val bulletPositionList: MutableList<Pair<UUID, MutableStateFlow<Pair<Float, Float>>>> =
        mutableListOf()

    private var bulletWatcherJob: Job = Job()

    private fun initEnemies() {
        enemyList.clear()
        repeat(4) { x ->

            val enemiesList = MutableList(4) { y ->
                Enemy.builder(measuredWidth, x, y)
            }

            val range = enemiesList.getRangeX()

            enemyList.add(
                EnemyColumn(
                    EnemyLocationRange(range.first, range.second),
                    enemiesList
                )
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initEnemies()
    }


    override fun onDraw(canvas: Canvas?) {
        enemyList.flattenedForEach {
            it.onDraw(canvas)
        }
    }

    fun checkCollision(bulletId: UUID, bulletPositionState: MutableStateFlow<Pair<Float, Float>>) {
        bulletPositionList.add(Pair(bulletId, bulletPositionState))
        bulletWatcherJob.cancelChildren()
        bulletWatcherJob = lifeCycleOwner.customViewLifeCycleScope.launch {

            bulletPositionList.forEach { bulletData ->

                launch {

                    bulletData.second.collect { bulletPosition ->

                        Log.d("Bullet before", " $bulletPosition")
                        enemyList.checkXForEach(bulletPosition.first) {
                            val enemyInLine = it.enemyList.reversed().find {
                                it.checkEnemyYPosition(bulletPosition.second)
                            }

                            enemyInLine?.let {
                                Log.d("Bullet", "${it.enemyX} ${it.enemyY} $bulletPosition")
                                destroyBullet(bulletData)
                                destroyEnemy(it)
                            }
                        }

                    }
                }
            }

        }
    }

    private fun destroyBullet(bulletData: Pair<UUID, MutableStateFlow<Pair<Float, Float>>>) {
        bulletPositionList.onEachIndexed { index, flow ->
            if (bulletData.first == flow.first) {
                onCollisionDetector?.onCollision(index)
                return@onEachIndexed
            }
        }
        removeBullet(bulletData.first)
    }

    private fun destroyEnemy(enemyInLine: Enemy) {
        enemyList.flattenedForEach {
            if (it == enemyInLine) {
                it.isVisible = false
            }
        }
        postInvalidate()
    }

    fun removeBullet(bullet: UUID) {
        val iterator = bulletPositionList.iterator()
        while (iterator.hasNext()) {
            val enemy = iterator.next()
            if (enemy.first == bullet) {
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