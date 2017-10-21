package com.jacquessmuts.motionsensordemo

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
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
    private val LIGHT_PIN = "GPIO_174"
    private val TAG = "MainActivity"

    private lateinit var textViewMain : TextView
    private lateinit var imageViewPhoto : ImageView

    private var motionSensorGpio: Gpio? = null
    private var lightGpio: Gpio? = null
    private var camera: CustomCamera? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewMain = findViewById(R.id.text_main)
        imageViewPhoto = findViewById(R.id.image_picture)

        start()
        setupCamera()
    }

    fun lightToggle(toggleOn: Boolean){

        lightGpio?.value = toggleOn
    }

    private fun setupCamera() {
        camera = CustomCamera.getInstance()
        camera?.initializeCamera(this, Handler(), imageAvailableListener)
    }

    private val imageAvailableListener = object : CustomCamera.ImageCapturedListener {
        override fun onImageCaptured(bitmap: Bitmap) {
            Log.d(TAG, "imageCaptured");
            imageViewPhoto.setImageBitmap(bitmap)
        }
    }

    fun start() {
        motionSensorGpio = PeripheralManagerService().openGpio(MOTION_SENSOR_PIN)
        motionSensorGpio?.setDirection(Gpio.DIRECTION_IN)
        motionSensorGpio?.setActiveType(Gpio.ACTIVE_HIGH)
        motionSensorGpio?.setEdgeTriggerType(Gpio.EDGE_BOTH)
        motionSensorGpio?.registerGpioCallback(object : GpioCallback() {
            override fun onGpioEdge(gpio: Gpio): Boolean {
                if (gpio.value) {
                    textViewMain.setText("MOTION OMG!")
                    Log.d(TAG, "taking photo...")
                    camera?.takePicture()
                } else {
                    textViewMain.setText("all is quiet...")
                }

                lightToggle(gpio.value)
                return true
            }
        })

        //Setup light
        lightGpio = PeripheralManagerService().openGpio(LIGHT_PIN)
        lightGpio?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
    }

    override fun onDestroy() {
        motionSensorGpio?.close()
        motionSensorGpio = null;
        lightGpio?.close()
        lightGpio = null
        super.onDestroy()
    }

}
