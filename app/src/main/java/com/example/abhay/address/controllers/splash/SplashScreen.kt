package com.example.abhay.address.controllers.splash

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.abhay.address.R
import com.example.abhay.address.controllers.display.AddressListDisplayActivity
import java.util.*

/**
 * It is the first screen that will be displayed after opening the application
 */
class SplashScreen : AppCompatActivity() {

    /**
     * will indicate the the current activity is in running state or not
     */
    private var isRunning: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (isRunning) {
                    startActivity(Intent(this@SplashScreen, AddressListDisplayActivity::class.java))
                    cancel()
                    finish()
                }
            }
        }, 3000, 1)
    }

    override fun onStart() {
        super.onStart()
        hide()
    }

    override fun onRestart() {
        super.onRestart()
        isRunning = true
    }

    override fun onStop() {
        super.onStop()
        isRunning = false
    }

    /**
     * This function will hide the system bar to show the activity in full screen.
     */
    private fun hide() {

        window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LOW_PROFILE

    }
}