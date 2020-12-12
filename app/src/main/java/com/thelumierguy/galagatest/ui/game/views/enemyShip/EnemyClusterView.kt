package com.thelumierguy.galagatest.ui.game.views.enemyShip

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import com.thelumierguy.galagatest.data.BulletStore
import com.thelumierguy.galagatest.data.GlobalCounter.enemyTimerFlow
import com.thelumierguy.galagatest.ui.base.BaseCustomView
import com.thelumierguy.galagatest.ui.game.views.bullets.BulletCoordinates
import com.thelumierguy.galagatest.utils.HapticService
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.random.Random


class EnemyClusterView(context: Context, attributeSet: AttributeSet? = null) :
    BaseCustomView(context, attributeSet) {

    companion object {
        var columnSize = 6

        var rowSize = 4

        var speed = 2F
    }

    lateinit var bulletStore: BulletStore

    private val hapticService by lazy { HapticService(context) }

    var onCollisionDetector: OnCollisionDetector? = null

    var enemyDetailsCallback: EnemyDetailsCallback? = null

    private var bulletWatcherJob: Job = Job()

    private val enemyList = mutableListOf(
        EnemyColumn()
    )

    var translateJob: Job = Job()

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
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        translateJob.cancel()
    }


    /**
     * Counter for translating the enemies
     */
    private fun startTranslating() {
        translateJob.cancel()
        translateJob = lifeCycleOwner.customViewLifeCycleScope.launch {
            enemyTimerFlow.collect {
                Log.d("ping ${System.identityHashCode(this)}", it.toString())
                enemyList.checkIfYReached(measuredHeight) { hasReachedMax ->
                    if (hasReachedMax) {
                        resetEnemies()
                    }
                    if (enemyList.isNotEmpty()) {
                        translateEnemy(System.currentTimeMillis())
                        fireCanon()
                        invalidate()
                    }
                }
            }
        }
    }

    private fun fireCanon() {
        if (System.currentTimeMillis() % 30 == 0L) {
            val enemyList: List<Enemy> = enemyList.flatMap {
                it.enemyList
            }
            val randomIndex = Random.nextInt(0, enemyList.size)
            if (randomIndex < enemyList.size) {
                val enemy = enemyList[randomIndex]
                if (enemy.isVisible)
                    enemyDetailsCallback?.onCanonReady(enemy.enemyX, enemy.enemyY)
            }
        }
    }

    private fun translateEnemy(millisUntilFinished: Long) {
        enemyList.flattenedForEach { enemy ->
            enemy.translate(millisUntilFinished)
        }
    }

    private fun resetEnemies() {
        enemyList.clear()
        enemyDetailsCallback?.onGameOver()
        hapticService.performHapticFeedback(320)
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
                                scanForEnemies()
                            }
                        }

                    }
                }
            }

        }
    }

    private fun scanForEnemies() {
        val anyVisible = enemyList.any {
            it.areAnyVisible()
        }
        if (!anyVisible) {
            hapticService.performHapticFeedback(320)
            enemyDetailsCallback?.onAllEliminated(bulletStore.getAmmoCount())
        }
    }

    private fun destroyBullet(bulletData: Pair<UUID, MutableStateFlow<BulletCoordinates>>) {
        bulletPositionList.onEachIndexed { index, flow ->
            if (bulletData.first == flow.first) {
                onCollisionDetector?.onCollision(bulletData.first)
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
        hapticService.performHapticFeedback(64, 48)
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

    fun startGame() {
        startTranslating()
    }

}

fun List<Enemy>.getRangeX(): Pair<Float, Float> {
    return if (size > 0) {
        val enemy = get(0)
        Pair(enemy.enemyX - enemy.hitBoxRadius, enemy.enemyX + enemy.hitBoxRadius)
    } else {
        Pair(0F, 0F)
    }
}

interface EnemyDetailsCallback {
    fun onAllEliminated(ammoCount: Int)
    fun onCanonReady(enemyX: Float, enemyY: Float)
    fun onGameOver()
}

interface OnCollisionDetector {
    fun onCollision(id: UUID)
}