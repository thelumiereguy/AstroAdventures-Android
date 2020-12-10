package com.thelumierguy.galagatest.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class AccelerometerManager(context: Context, val onUpdateCallBack: (SensorEvent) -> Unit) {

    private val gyroscopeSensor: Sensor by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private val sensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private var gyroscopeSensorListener: SensorEventListener? = null

    fun startListening() {
        gyroscopeSensorListener = object : SensorEventListener {
            override fun onSensorChanged(sensorEvent: SensorEvent) {
                onUpdateCallBack(sensorEvent)
            }

            override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
        }
        sensorManager.registerListener(
            gyroscopeSensorListener,
            gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME
        )
    }


    fun stopListening() {
        sensorManager.unregisterListener(gyroscopeSensorListener)
    }
}