package com.example.abhay.address

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import kotlinx.android.synthetic.main.activity_splash_screen.*

/**
 * It is the first screen that will be displayed after opening the application
 */
class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_screen)
        hide()
        Thread {
            Thread.sleep(3000)
            Handler(mainLooper).post {
                startActivity(Intent(this, BaseActivity::class.java))
                finish()
            }
        }.start()
        //startActivity(Intent(this, BaseActivity::class.java))
        //startActivity(Intent(this, AddOrEditAddressActivity::class.java))
        //finish()
    }

    /**
     * This function will hide the action bar.
     */
    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()

        fullscreen_content.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
}