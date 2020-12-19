package com.thelumierguy.astroadventures.ui.game.views.playership

import android.content.Context
import android.graphics.*
import android.graphics.drawable.PictureDrawable
import android.hardware.SensorEvent
import android.util.AttributeSet
import android.util.Range
import com.thelumierguy.astroadventures.R
import com.thelumierguy.astroadventures.data.*
import com.thelumierguy.astroadventures.ui.base.BaseCustomView
import com.thelumierguy.astroadventures.ui.game.views.enemyShip.OnCollisionCallBack
import com.thelumierguy.astroadventures.utils.AccelerometerManager
import com.thelumierguy.astroadventures.utils.HapticService
import com.thelumierguy.astroadventures.utils.lowPass
import com.thelumierguy.astroadventures.utils.map
import java.util.*
import kotlin.math.roundToInt


class SpaceShipView(context: Context, attributeSet: AttributeSet? = null) :
    BaseCustomView(context, attributeSet), RigidBodyObject {

    var onCollisionCallBack: OnCollisionCallBack? = null
        set(value) {
            field = value
            collisionDetector.onCollisionCallBack = value
        }

    override val collisionDetector: CollisionDetector = CollisionDetector(lifeCycleOwner)

    private var accelerometerManager: AccelerometerManager? = null

    lateinit var bulletStore: BulletStore

    private var currentShipPosition: Float = 0F

    private val bodyPaint = Paint().apply {
        color = Color.parseColor("#DEDEDE")
        isAntiAlias = false
        isDither = false
    }

    private val bodyPaintStroke = Paint().apply {
        color = Color.parseColor("#DEDEDE")
        style = Paint.Style.STROKE
        isAntiAlias = false
        isDither = false
    }


    private val wingsPaintOutline = Paint().apply {
        color = Color.parseColor("#0069DE")
        style = Paint.Style.STROKE
        strokeWidth = 2F
        isAntiAlias = false
        isDither = false
    }

    private val jetPaint = Paint().apply {
        color = Color.parseColor("#F24423")
        isAntiAlias = false
        strokeWidth = 8F
        isDither = false
        setShadowLayer(10F, 0F, 10F, Color.MAGENTA)
    }

    private var streamLinedTopPoint = 0f
    private var bodyTopPoint = 0f
    private var wingWidth = 0F
    private var halfWidth = 0F
    private var halfHeight = 0F
    private var missileSize = 0F

    private lateinit var spaceShipPicture: Picture

    private lateinit var pictureDrawable: PictureDrawable

    private var gravityValue = FloatArray(1)

    private var translationXValue = 0F

    private val hapticService by lazy { HapticService(context) }

    private var displayRect = Rect()

    var processAccelerometerValues = true

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.SpaceShipView,
            0, 0).apply {

            try {
                processAccelerometerValues =
                    getBoolean(R.styleable.SpaceShipView_processAccelerometer, true)
            } finally {
                recycle()
            }
        }
    }

    private fun initPicture() {
        spaceShipPicture = Picture()
        pictureDrawable = PictureDrawable(spaceShipPicture)
        val canvas = spaceShipPicture.beginRecording(measuredWidth, measuredHeight)
        canvas.let {
            drawExhaust(it)
            drawStreamlinedBody(it)
            drawBody(it)
            drawMisc(it)
            drawShipWings(it)

        }
        spaceShipPicture.endRecording()

        pictureDrawable = PictureDrawable(spaceShipPicture)

        postInvalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        accelerometerManager?.stopListening()
    }

    override fun onDraw(canvas: Canvas?) {
        if (isInEditMode) {
            return
        }
        canvas?.let {
            pictureDrawable.bounds = displayRect
            pictureDrawable.draw(canvas)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        halfWidth = w / 2F
        getDrawingRect(displayRect)
        halfHeight = h / 2F
        currentShipPosition = halfWidth
        streamLinedTopPoint = h / 4F
        bodyTopPoint = h / 3F
        wingWidth = w / 15F
        missileSize = h / 8F
        shipYRange = Range(top + streamLinedTopPoint, top + (2 * streamLinedTopPoint))
        initPicture()
    }

    private fun drawStreamlinedBody(it: Canvas) {
        bodyPaintStroke.strokeWidth = 10F
        it.drawLine(
            halfWidth,
            streamLinedTopPoint,
            halfWidth,
            measuredHeight - streamLinedTopPoint,
            bodyPaintStroke
        )
    }


    fun getShipX() = currentShipPosition

    private fun drawBody(it: Canvas) {
        bodyPaintStroke.strokeWidth = 24F
        it.drawLine(
            halfWidth,
            bodyTopPoint,
            halfWidth,
            measuredHeight - bodyTopPoint,
            bodyPaintStroke
        )
    }

    private fun drawMisc(canvas: Canvas) {
        var startY = halfHeight + bodyTopPoint
        var startX = halfWidth - wingWidth
        canvas.drawMissile(startX, startY)

        startX = halfWidth + wingWidth
        canvas.drawMissile(startX, startY)

        startX = (halfWidth - wingWidth / 2)
        startY = (halfHeight + bodyTopPoint / 3F)
        canvas.drawMissile(startX, startY)

        startX = (halfWidth + wingWidth / 2)
        canvas.drawMissile(startX, startY)
    }


    private fun drawExhaust(canvas: Canvas) {
        val path = Path()

        val topPoint = halfHeight + streamLinedTopPoint / 2

        path.moveTo(
            halfWidth,
            topPoint
        ) // Top

        path.lineTo(
            halfWidth - wingWidth / 10,
            topPoint
        )

        path.lineTo(
            halfWidth - wingWidth / 5,
            halfHeight + streamLinedTopPoint
        )

        path.lineTo(
            halfWidth,
            measuredHeight - bodyTopPoint
        )

        path.moveTo(
            halfWidth + wingWidth / 10,
            topPoint
        ) // Top

        path.lineTo(
            halfWidth + wingWidth / 5,
            halfHeight + streamLinedTopPoint
        )

        path.lineTo(
            halfWidth,
            measuredHeight - bodyTopPoint
        )

        path.close()

        canvas.drawPath(path, jetPaint)
    }

    private fun Canvas.drawMissile(startX: Float, startY: Float) {
        drawLine(
            startX,
            startY,
            startX,
            startY - missileSize,
            jetPaint
        )
    }


    private fun drawShipWings(canvas: Canvas) {
        val path = Path()

        path.moveTo(halfWidth, halfHeight - bodyTopPoint / 3) // Top

        path.lineTo(
            halfWidth - wingWidth,
            halfHeight + bodyTopPoint
        ) // Left

        path.lineTo(
            halfWidth,
            halfHeight + streamLinedTopPoint / 2
        ) // Return to mid

        path.lineTo(
            halfWidth + wingWidth,
            halfHeight + bodyTopPoint
        ) // Right

        path.close()

        canvas.drawPath(path, bodyPaint)
        canvas.drawPath(path, wingsPaintOutline)

    }

    fun getShipY(): Float = bodyTopPoint

    private fun processSensorEvents(sensorEvent: SensorEvent) {
        lowPass(sensorEvent.values, gravityValue)
        if (processAccelerometerValues) {
            processValues()
        } else {
            if (gravityValue[0] < -3F || gravityValue[0] > 3F) {
                processAccelerometerValues = true
                if (!isCallBackInvoked) {
                    gravityValue[0] = 0F
                    levelZeroCallBackPlayer?.onTilted()
                }
            }
        }
    }

    private fun processValues() {
        translationXValue = map(gravityValue[0], 6F, -6F, -wingWidth, measuredWidth + wingWidth)
        if (translationXValue > wingWidth && translationXValue < measuredWidth - wingWidth) {
            currentShipPosition = translationXValue
            shipXRange = Range(currentShipPosition - wingWidth,
                currentShipPosition + wingWidth)
            displayRect.set(
                (translationXValue - halfWidth).roundToInt(),
                0,
                (translationXValue + halfWidth).roundToInt(),
                measuredHeight
            )
            invalidate()
        }
    }

    private fun addAccelerometerListener() {
        accelerometerManager = AccelerometerManager(context.applicationContext) { sensorEvent ->
            processSensorEvents(sensorEvent)
        }
    }

    fun startGame() {
        addAccelerometerListener()
        accelerometerManager?.startListening()
    }

    private var shipXRange = Range(0F, 0F)
    private var shipYRange = Range(0F, 0F)


    override fun checkCollision(
        softBodyObjectData: SoftBodyObjectData,
    ) {
        collisionDetector.checkCollision(softBodyObjectData) { softBodyPosition, softBodyObject ->

            if (softBodyPosition.y.roundToInt() > top) {
                if (shipYRange.contains(softBodyPosition.y))
                    if (shipXRange.contains(softBodyPosition.x)) {
                        onPlayerHit(softBodyObject)
                    }
            }

        }
    }

    private fun onPlayerHit(softBodyObject: SoftBodyObjectData) {
        when (softBodyObject.objectType) {
            SoftBodyObjectType.BULLET -> {
                hapticService.performHapticFeedback(64, 48)
                PlayerHealthInfo.onHit()
            }
            is SoftBodyObjectType.DROP -> {
                when (softBodyObject.objectType.dropType) {
                    is DropType.Ammo -> {
                        hapticService.performHapticFeedback(128, 48)
                        if (::bulletStore.isInitialized)
                            bulletStore.addAmmo(softBodyObject.objectType.dropType.ammoCount)
                    }
                }
            }
        }
        collisionDetector.onHitRigidBody(softBodyObject)

    }

    override fun removeSoftBodyEntry(bullet: UUID) {
        collisionDetector.removeSoftBodyEntry(bullet)
    }

    var levelZeroCallBackPlayer: LevelZeroCallBackPlayer? = null
        set(value) {
            field = value
            isCallBackInvoked = false
        }

    private var isCallBackInvoked = true
}

interface LevelZeroCallBackPlayer {
    fun onTilted()
}