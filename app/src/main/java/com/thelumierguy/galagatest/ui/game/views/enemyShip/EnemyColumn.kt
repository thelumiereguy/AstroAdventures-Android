package com.thelumierguy.galagatest.ui.game.views.enemyShip

import android.util.Range

typealias EnemyLocationRange = Range<Float>

data class EnemyColumn(
    val range: EnemyLocationRange = EnemyLocationRange(0F, 0F),
    val enemyList: List<Enemy> = listOf(),
) {

    fun areAnyVisible(): Boolean {
        return enemyList.any { it.isVisible }
    }
}

inline fun List<EnemyColumn>.checkXForEach(x: Float, transform: (EnemyColumn) -> Unit) {
    val iterator = iterator()
    while (iterator.hasNext()) {
        val enemyColumn = iterator.next()
        if (enemyColumn.range.contains(x) && enemyColumn.areAnyVisible()) {
            transform(enemyColumn)
            return
        }
    }
}

inline fun MutableList<EnemyColumn>.flattenedForEach(transform: (Enemy) -> Unit) {
    flatMap { it.enemyList }.forEach {
        transform(it)
    }
}

inline fun MutableList<EnemyColumn>.checkIfYReached(maxHeight: Int, transform: (Boolean) -> Unit) {
    transform(
        flatMap { it.enemyList }.any {
            (it.enemyY + it.hitBoxRadius) > maxHeight && it.isVisible
        })
}

