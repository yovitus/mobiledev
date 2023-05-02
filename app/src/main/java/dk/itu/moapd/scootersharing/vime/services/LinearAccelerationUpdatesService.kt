package dk.itu.moapd.scootersharing.vime.services

import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.IBinder
import kotlin.math.sqrt


class LinearAccelerationUpdatesService : Service() {
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null

    private var updateFunc: (Float) -> Unit = { _: Float -> }

    private val sensorEventListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
                val accelerationX = event.values[0]
                val accelerationY = event.values[1]
                val accelerationCombined =
                    sqrt((accelerationX * accelerationX) + (accelerationY * accelerationY))
                updateFunc(accelerationCombined)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    override fun onBind(intent: Intent?): IBinder {
        startAccelerationAware()
        return LocalBinder()
    }

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods.
        fun getService(): LinearAccelerationUpdatesService = this@LinearAccelerationUpdatesService
    }

    private fun startAccelerationAware() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager?

        if (sensorManager != null)
            accelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    }

    fun subscribeToAccelerationUpdates(
        upFunc: (Float) -> Unit
    ) {
        updateFunc = upFunc

        sensorManager?.registerListener(
            sensorEventListener,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun unsubscribeToAccelerationUpdates() {
        sensorManager?.unregisterListener(sensorEventListener)
    }
}