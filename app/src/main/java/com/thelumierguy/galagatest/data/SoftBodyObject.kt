package com.thelumierguy.galagatest.data

import android.graphics.Canvas
import com.thelumierguy.galagatest.ui.game.views.bullets.BulletView
import com.thelumierguy.galagatest.ui.game.views.bullets.SoftBodyCoordinates
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

abstract class SoftBodyObject(
    objectX: Float,
    initY: Float,
    private val sender: BulletView.Sender,
    private val maxHeight: Int,
    private val softBodyObjectTracker: SoftBodyObjectTracker?,
    softBodyObjectType: SoftBodyObjectType,
) {

    val id: UUID = UUID.randomUUID()

    private var objectY = initY

    fun getObjectY() = objectY

    protected var bulletPosition =
        MutableStateFlow(SoftBodyCoordinates(objectX, objectY))


    init {
        softBodyObjectTracker?.initBulletTracking(SoftBodyObjectData(id,
            bulletPosition,
            sender,
            softBodyObjectType))
    }

    abstract val speed: Int


    abstract fun drawObject(canvas: Canvas)

    abstract fun translateObject()

    fun translate() {
        if (sender == BulletView.Sender.PLAYER) {
            objectY -= speed
            if (objectY < 0) {
                softBodyObjectTracker?.cancelTracking(id, sender)
            }
        } else {
            objectY += speed
            if (objectY > maxHeight) {
                softBodyObjectTracker?.cancelTracking(id, sender)
            }
        }
    }

    interface SoftBodyObjectTracker {
        fun initBulletTracking(
            softBodyObjectData: SoftBodyObjectData,
        )

        fun cancelTracking(bulletId: UUID, sender: BulletView.Sender)
    }
}

data class SoftBodyObjectData(
    val objectId: UUID, val softBodyPosition: MutableStateFlow<SoftBodyCoordinates>,
    val sender: BulletView.Sender, val objectType: SoftBodyObjectType,
)

sealed class SoftBodyObjectType {
    object BULLET : SoftBodyObjectType()
    data class DROP(val dropType: DropType) : SoftBodyObjectType()
}

sealed class DropType {
    data class Ammo(val ammoCount: Int) : DropType()
}