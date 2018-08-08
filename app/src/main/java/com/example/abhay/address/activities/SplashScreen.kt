package com.example.abhay.address.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.abhay.address.R

/**
 * It is the first screen that will be displayed after opening the application
 */
class SplashScreen : AppCompatActivity() {

    private var startBaseActivity: (() -> Unit)? = null
    private var isRunning: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_screen)

        Thread {
            //Thread.sleep(3000)
            startBaseActivity = {
                startActivity(Intent(this, BaseActivity::class.java))
                finish()
            }
            if (isRunning) {
                //Handler(mainLooper).post {
                    startBaseActivity?.invoke()
                //}
            }
        }.start()
        //startActivity(Intent(this, BaseActivity::class.java))
        //startActivity(Intent(this, AddOrEditAddressActivity::class.java))
        //finish()
    }

    override fun onStart() {
        super.onStart()
        hide()
    }

    override fun onRestart() {
        super.onRestart()
        isRunning = true
        if (startBaseActivity != null)
            startBaseActivity?.invoke()
    }

    override fun onStop() {
        super.onStop()
        isRunning = false
    }

    /**
     * This function will hide the action bar.
     */
    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()

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