package com.example.abhay.address

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * It is the first screen that will be displayed after opening the application
 */
class SplashScreen : AppCompatActivity() {

    var list: Array<Address>? = null

    interface ListReceivedListener {
        fun onDataReceived()
    }

    private var listReceivedListener: ListReceivedListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        hide()

        Thread {
            fun startBaseActivity() {
                Handler(mainLooper).post {
                    startActivity(Intent(this@SplashScreen, BaseActivity::class.java).apply {
                        putExtra("addresses", list)
                    })
                    finish()
                }
            }

            Thread.sleep(3000)
            synchronized(Unit) {
                if (list == null) {
                    listReceivedListener = object : ListReceivedListener {
                        override fun onDataReceived() {
                            startBaseActivity()
                        }
                    }
                } else {
                    startBaseActivity()
                }
            }
        }.start()

        fetchData()
        //startActivity(Intent(this, BaseActivity::class.java))
        //startActivity(Intent(this, AddOrEditAddressActivity::class.java))
        //finish()
    }

    /**
     * This function fetches the addresses from the server using Retrofit
     */
    private fun fetchData() {
        val call = RetrofitClient.client.getAllAddresses()

        call.enqueue(object : Callback<Array<Address>> {

            override fun onResponse(call: Call<Array<Address>>?, response: Response<Array<Address>>?) {
                Log.d("received", response.toString())
                Log.d("data", response?.body().toString())
                list = response?.body()
                //Toast.makeText(this@BaseActivity, list?.size.toString(), Toast.LENGTH_SHORT).show()

                list?.indices?.forEach {
                    Log.d(it.toString(), list!![it].toString())
                }
                synchronized(Unit) {
                    if (listReceivedListener != null) {
                        listReceivedListener?.onDataReceived()
                    }
                }
            }

            override fun onFailure(call: Call<Array<Address>>?, t: Throwable?) {
                Toast.makeText(this@SplashScreen, "error occcurred", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * This function will hide the action bar.
     */
    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()

        findViewById<ImageView>(R.id.fullscreen_content).systemUiVisibility =
                //window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

    }
}