package com.thelumierguy.galagatest.data

import com.thelumierguy.galagatest.ui.game.views.bullets.SoftBodyCoordinates
import com.thelumierguy.galagatest.ui.game.views.enemyShip.OnCollisionCallBack
import com.thelumierguy.galagatest.utils.CustomLifeCycleOwner
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

interface RigidBodyObject {
    val collisionDetector: CollisionDetector
    fun removeSoftBodyEntry(bullet: UUID)
    fun checkCollision(softBodyObjectData: SoftBodyObjectData)
}


class CollisionDetector(
    private val lifeCycleOwner: CustomLifeCycleOwner,
) {

    var onCollisionCallBack: OnCollisionCallBack? = null

    private var bulletWatcherJob: Job = Job()

    private val softBodyPositionList: MutableList<SoftBodyObjectData> =
        mutableListOf()

    fun checkCollision(
        softBodyObjectData: SoftBodyObjectData,
        newPositionCollected: (SoftBodyCoordinates, SoftBodyObjectData) -> Unit,
    ) {
        softBodyPositionList.add(softBodyObjectData)
        bulletWatcherJob.cancelChildren()
        bulletWatcherJob = lifeCycleOwner.customViewLifeCycleScope.launch {
            softBodyPositionList.forEach { softBodyData ->
                launch {
                    softBodyData.softBodyPosition.collect { softBodyCoordinates ->
                        newPositionCollected(softBodyCoordinates, softBodyData)
                    }
                }
            }

        }
    }

    fun onHitRigidBody(softBodyObject: SoftBodyObjectData) {
        softBodyPositionList.forEach { softBodyObj ->
            if (softBodyObject.objectId == softBodyObj.objectId) {
                onCollisionCallBack?.onCollision(softBodyObject)
            }
        }
        removeSoftBodyEntry(softBodyObject.objectId)
    }


    fun removeSoftBodyEntry(bullet: UUID) {
        val iterator = softBodyPositionList.iterator()
        while (iterator.hasNext()) {
            val enemy = iterator.next()
            if (enemy.objectId == bullet) {
                iterator.remove()
            }
        }
    }
}