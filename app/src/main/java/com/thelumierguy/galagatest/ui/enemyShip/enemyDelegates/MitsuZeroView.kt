package com.thelumierguy.galagatest.ui.enemyShip.enemyDelegates

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Picture
import android.graphics.drawable.PictureDrawable
import androidx.core.graphics.toRect
import com.thelumierguy.galagatest.ui.enemyShip.EnemyAirShip
import com.thelumierguy.galagatest.ui.enemyShip.GetAirShipData
import kotlin.math.roundToInt


class MitsuZeroView(private val getAirShipData: GetAirShipData) : EnemyAirShip {

    private val rudderPaint = Paint().apply {
        color = Color.parseColor("#7C8586")
        isAntiAlias = false
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
        isDither = false
    }

    private val bodyPaint = Paint().apply {
        color = Color.parseColor("#707146")
        isAntiAlias = false
        strokeJoin = Paint.Join.ROUND
        style = Paint.Style.FILL_AND_STROKE
        isDither = false
    }

    private val cockPitPaint = Paint().apply {
        color = Color.parseColor("#0F100D")
        isAntiAlias = false
        isDither = false
    }


    private val wingsPaint = Paint().apply {
        color = Color.parseColor("#7C8586")
        isAntiAlias = false
        strokeJoin = Paint.Join.ROUND
        style = Paint.Style.FILL_AND_STROKE
        isDither = false
    }

    private val engineTip = Paint().apply {
        color = Color.parseColor("#842D21")
        isAntiAlias = false
        isDither = false
    }

    private var halfWidth = 0F
    private var halfHeight = 0F


    private lateinit var spaceShipPicture: Picture
    private lateinit var pictureDrawable: PictureDrawable

    private fun initPicture() {
        spaceShipPicture = Picture()
        val canvas = spaceShipPicture.beginRecording(
            getAirShipData.getAirshipDrawRect().width().roundToInt(),
            getAirShipData.getAirshipDrawRect().height().roundToInt()
        )
        canvas?.let {
            drawStreamlinedBody(it)
        }
        spaceShipPicture.endRecording()

        pictureDrawable = PictureDrawable(spaceShipPicture)
    }

    private fun drawStreamlinedBody(canvas: Canvas) {

    }

    override fun init() {
        calculateSize()
        initPicture()
    }

    private fun calculateSize() {
        val height = getAirShipData.getAirshipDrawRect().height()
        val width = getAirShipData.getAirshipDrawRect().width()
        halfHeight = height / 2
        halfWidth = width / 2
    }

    override fun draw(canvas: Canvas) {
        pictureDrawable.bounds = getAirShipData.getAirshipDrawRect().toRect()
        pictureDrawable.draw(canvas)
    }

}