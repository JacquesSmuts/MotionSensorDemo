package com.jacquessmuts.motionsensordemo

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import com.google.android.things.pio.PeripheralManagerService

/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : Activity() {

    private val MOTION_SENSOR_PIN = "GPIO_35"
    private val TAG = "MainActivity"

    private lateinit var textViewMain : TextView

    private var motionSensorGpio: Gpio? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewMain = findViewById(R.id.text_main)

        start();
    }


    fun start() {
        motionSensorGpio = PeripheralManagerService().openGpio(MOTION_SENSOR_PIN)
        motionSensorGpio?.setDirection(Gpio.DIRECTION_IN)
        motionSensorGpio?.setActiveType(Gpio.ACTIVE_HIGH)
        motionSensorGpio?.setEdgeTriggerType(Gpio.EDGE_BOTH)
        motionSensorGpio?.registerGpioCallback(object : GpioCallback() {
            override fun onGpioEdge(gpio: Gpio): Boolean {
                if (gpio.value) {
                    Log.d(TAG, "onMotionEdge")
                    textViewMain.setText("MOTION OMG!")
                    //motionListener.onMotionDetected()
                } else {
                    Log.d(TAG, "no Motion Detected")
                    textViewMain.setText("all is quiet...")
                    //motionListener.onMotionStopped()
                }
                return true
            }
        })
    }

    override fun onDestroy() {
        motionSensorGpio = null;
        super.onDestroy()
    }

}
