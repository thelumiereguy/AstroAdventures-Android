package com.thelumierguy.galagatest.ui.enemyShip


data class EnemyColumn(
    val range: EnemyLocationRange = EnemyLocationRange(0F, 0F),
    val enemyList: List<EnemiesView.Enemy> = listOf()
) {

}

inline fun List<EnemyColumn>.forEach(transform: (EnemyColumn) -> Unit) {
    val iterator = iterator()
    while (iterator.hasNext()) {
        val enemy = iterator.next()
        transform(enemy)
    }
}

inline fun List<EnemyColumn>.checkXForEach(x: Float, transform: (EnemyColumn) -> Unit) {
    val iterator = iterator()
    while (iterator.hasNext()) {
        val enemy = iterator.next()
        if (enemy.range.contains(x)) {
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

