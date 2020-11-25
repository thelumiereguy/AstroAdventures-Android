package com.thelumierguy.galagatest.ui.enemyShip

import android.content.Context
import android.graphics.Canvas
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.Log
import com.thelumierguy.galagatest.ui.base.BaseCustomView
import com.thelumierguy.galagatest.ui.bullets.BulletCoordinates
import com.thelumierguy.galagatest.ui.enemyShip.enemy.Enemy
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*


class EnemiesView(context: Context, attributeSet: AttributeSet? = null) :
    BaseCustomView(context, attributeSet) {

    companion object {
        var columnSize = 4

        var rowSize = 4
    }

    var onCollisionDetector: OnCollisionDetector? = null

    private var bulletWatcherJob: Job = Job()

    private val enemyList = mutableListOf(
        EnemyColumn()
    )

    private var timer: CountDownTimer? = null

    private val bulletPositionList: MutableList<Pair<UUID, MutableStateFlow<BulletCoordinates>>> =
        mutableListOf()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initEnemies()
    }

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    private fun initEnemies() {
        enemyList.clear()
        repeat(columnSize) { x ->

            val enemiesList = MutableList(rowSize) { y ->
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

        startTranslating()
    }

    private fun startTranslating() {
        timer = object : CountDownTimer(50000, 50) {
            override fun onTick(millisUntilFinished: Long) {
                enemyList.checkIfYReached(measuredHeight) { hasReachedMax ->
                    if (hasReachedMax) {
                        resetEnemies()
                    }
                    if (enemyList.isNotEmpty()) {
                        translateEnemy()
                        invalidate()
                    }
                }
            }

            private fun translateEnemy() {
                enemyList.flattenedForEach { enemy ->
                    enemy.enemyY = enemy.enemyY + 2F
                    enemy.drawRect.offset(0F, 2F)
                }
            }

            override fun onFinish() {
                startTranslating()
            }

        }.start()
    }

    private fun resetEnemies() {
        timer?.cancel()
        enemyList.clear()
        postInvalidate()
    }


    override fun onDraw(canvas: Canvas?) {
        enemyList.flattenedForEach {
            it.onDraw(canvas)
        }
    }

    fun checkCollision(bulletId: UUID, bulletPositionState: MutableStateFlow<BulletCoordinates>) {
        bulletPositionList.add(Pair(bulletId, bulletPositionState))
        bulletWatcherJob.cancelChildren()
        bulletWatcherJob = lifeCycleOwner.customViewLifeCycleScope.launch {

            bulletPositionList.forEach { bulletData ->

                launch {

                    bulletData.second.collect { bulletPosition ->

                        enemyList.checkXForEach(bulletPosition.x) {
                            val enemyInLine = it.enemyList.reversed().find {
                                it.checkEnemyYPosition(bulletPosition.y)
                            }

                            enemyInLine?.let { enemy ->
                                Log.d("Bullet", "${enemy.enemyX} ${enemy.enemyY} $bulletPosition")
                                destroyBullet(bulletData)
                                destroyEnemy(enemy)
                            }
                        }

                    }
                }
            }

        }
    }

    private fun destroyBullet(bulletData: Pair<UUID, MutableStateFlow<BulletCoordinates>>) {
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
                it.onHit()
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

}

fun List<Enemy>.getRangeX(): Pair<Float, Float> {
    return if (size > 0) {
        val enemy = get(0)
        Pair(enemy.enemyX - enemy.radius, enemy.enemyX + enemy.radius)
    } else {
        Pair(0F, 0F)
    }
}

interface OnCollisionDetector {
    fun onCollision(index: Int)
}