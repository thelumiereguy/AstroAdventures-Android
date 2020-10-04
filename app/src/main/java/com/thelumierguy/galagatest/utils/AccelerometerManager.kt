package com.thelumierguy.galagatest.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class AccelerometerManager(context: Context, onUpdateCallBack: (SensorEvent) -> Unit) : LifecycleObserver {

    private val gyroscopeSensor: Sensor by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private val sensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private val gyroscopeSensorListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent) {
            onUpdateCallBack(sensorEvent)
        }

        override fun onAccuracyChanged(sensor: Sensor, i: Int) {}
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun startListening() {
        sensorManager.registerListener(
            gyroscopeSensorListener,
            gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopListening() {
        sensorManager.unregisterListener(gyroscopeSensorListener)
    }
}