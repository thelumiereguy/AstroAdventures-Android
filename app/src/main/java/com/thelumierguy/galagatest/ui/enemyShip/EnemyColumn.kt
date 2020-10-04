package com.thelumierguy.galagatest.ui.enemyShip


data class EnemyColumn(
    val range: EnemyLocationRange = EnemyLocationRange(0F, 0F),
    val enemyList: List<EnemiesView.Enemy> = listOf(),
) {

    fun areAnyVisible(): Boolean {
        return enemyList.any { it.isVisible }
    }
}

inline fun List<EnemyColumn>.checkXForEach(x: Float, transform: (EnemyColumn) -> Unit) {
    val iterator = iterator()
    while (iterator.hasNext()) {
        val enemy = iterator.next()
        if (enemy.range.contains(x) && enemy.areAnyVisible()) {
            transform(enemy)
            return
        }
    }
}

inline fun MutableList<EnemyColumn>.flattenedForEach(transform: (EnemiesView.Enemy) -> Unit) {
    flatMap { it.enemyList }.forEach {
        transform(it)
    }
}

inline fun MutableList<EnemyColumn>.checkIfYReached(maxHeight: Int, transform: (Boolean) -> Unit) {
    transform(
        flatMap { it.enemyList }.any {
            (it.enemyY + it.radius) > maxHeight && it.isVisible
        })
}

