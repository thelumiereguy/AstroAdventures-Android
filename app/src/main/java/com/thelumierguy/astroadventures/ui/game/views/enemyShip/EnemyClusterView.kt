package com.thelumierguy.astroadventures.ui.game.views.enemyShip

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.thelumierguy.astroadventures.R
import com.thelumierguy.astroadventures.data.*
import com.thelumierguy.astroadventures.data.GlobalCounter.enemyTimerFlow
import com.thelumierguy.astroadventures.ui.base.BaseCustomView
import com.thelumierguy.astroadventures.utils.HapticService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToInt


class EnemyClusterView(context: Context, attributeSet: AttributeSet? = null) :
    BaseCustomView(context, attributeSet), RigidBodyObject {

    companion object {
        var speed = 2F
    }

    var columnSize = 6

    var rowSize = 4

    lateinit var bulletStore: BulletStore

    private val hapticService by lazy { HapticService(context) }

    var onCollisionCallBack: OnCollisionCallBack? = null
        set(value) {
            field = value
            collisionDetector.onCollisionCallBack = value
        }

    override val collisionDetector: CollisionDetector = CollisionDetector(lifeCycleOwner)

    var enemyDetailsCallback: EnemyDetailsCallback? = null

    private val enemyList = mutableListOf(
        EnemyColumn()
    )

    private var translateJob: Job = Job()

    private var firingJob: Job = Job()


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initEnemies()
    }

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.EnemyClusterView,
            0, 0).apply {
            try {
                rowSize =
                    getFloat(R.styleable.EnemyClusterView_enemiesRows,
                        rowSize.toFloat()).roundToInt()
                columnSize =
                    getFloat(R.styleable.EnemyClusterView_enemiesColumns,
                        columnSize.toFloat()).roundToInt()
            } finally {
                recycle()
            }
        }
    }

    private fun initEnemies() {
        enemyList.clear()
        repeat(columnSize) { x ->

            val enemiesList = MutableList(rowSize) { y ->
                Enemy.builder(columnSize, measuredWidth, x, y)
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
        firingJob.cancel()
    }


    /**
     * Counter for translating the enemies
     */
    private fun startTranslating() {
        translateJob.cancel()
        translateJob = lifeCycleOwner.customViewLifeCycleScope.launch {
            enemyTimerFlow.collect {
                enemyList.checkIfYReached(measuredHeight) { hasReachedMax ->
                    if (hasReachedMax) {
                        resetEnemies()
                    }
                    if (enemyList.isNotEmpty()) {
                        translateEnemy(System.currentTimeMillis())
                        invalidate()
                    }
                }
            }
        }
    }

    private fun fireCanon() {
        if (shouldEmitObjects()) {
            firingJob.cancel()
            firingJob = lifeCycleOwner.customViewLifeCycleScope.launch {
                ticker(1000, 200, Dispatchers.Default).receiveAsFlow().collect {
                    val enemyList = enemyList.random()
                    val enemy = enemyList.enemyList.findLast { it.isVisible }
                    enemy?.let {
                        enemyDetailsCallback?.onCanonReady(enemy.enemyX, enemy.enemyY)
                    }
                }
            }
        }
    }

    private fun shouldEmitObjects(): Boolean = LevelInfo.level != 0

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

    override fun checkCollision(
        softBodyObjectData: SoftBodyObjectData,
    ) {
        collisionDetector.checkCollision(softBodyObjectData) { softBodyPosition, softBodyObject ->

            enemyList.checkXForEach(softBodyPosition.x) {
                val enemyInLine = it.enemyList.reversed().find {
                    it.checkEnemyYPosition(softBodyPosition.y)
                }

                enemyInLine?.let { enemy ->
                    collisionDetector.onHitRigidBody(softBodyObject)
                    destroyEnemy(enemy)
                    scanForEnemies()
                }
            }

        }
    }

    override fun removeSoftBodyEntry(bullet: UUID) {
        collisionDetector.removeSoftBodyEntry(bullet)
    }

    private fun scanForEnemies() {
        val anyVisible = enemyList.any {
            it.areAnyVisible()
        }
        if (!anyVisible) {
            hapticService.performHapticFeedback(320)
            if (::bulletStore.isInitialized)
                enemyDetailsCallback?.onAllEliminated(bulletStore.getAmmoCount())
        }
    }

    private fun destroyEnemy(enemyInLine: Enemy) {
        enemyList.flattenedForEach {
            if (it == enemyInLine) {
                it.onHit()
            }
        }
        dropGift(enemyInLine)
        hapticService.performHapticFeedback(64, 48)
        postInvalidate()
    }

    private fun dropGift(enemyInLine: Enemy) {
        if (enemyInLine.hasDrops && enemyInLine.enemyLife == 0 && shouldEmitObjects()) {
            enemyDetailsCallback?.hasDrop(enemyInLine.enemyX, enemyInLine.enemyY)
        }
    }


    fun startGame() {
        startTranslating()
        fireCanon()
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
    fun hasDrop(enemyX: Float, enemyY: Float)
    fun onGameOver()
}

interface OnCollisionCallBack {
    fun onCollision(softBodyObject: SoftBodyObjectData)
}