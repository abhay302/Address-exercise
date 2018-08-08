package com.example.abhay.address.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.abhay.address.R
import com.example.abhay.address.fragments.BlankAddressFragment
import com.example.abhay.address.fragments.DisplayAddressFragment
import com.example.abhay.address.network.Address
import com.example.abhay.address.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * This activty will display a list of addresses
 * It can display two types of fragments:
 * 1. BlankAddressFragment: If no address is present
 * 2. DisplayAddressFragment: Will display a list of addresses in this fragment
 */

class BaseActivity : AppCompatActivity(), DisplayAddressFragment.EmptyListCallback {

    /**
     * will be called to show a particular fragment depending upon the no. of elements in the address list.
     */
    private var showFragment: (() -> Unit)? = null

    /**
     * Will indicate whether the current activity is in resumed state
     */
    private var isRunning: Boolean = true

    var list: Array<Address>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        fetchData()
        val toolbar = findViewById<android.support.v7.widget.Toolbar>(R.id.my_toolbar)
        //toolbar.setTitleTextColor(Color.argb(0.8444f, 255f,255f,255f))
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //fetchData()
        if (intent?.extras?.get("address") != null) {
            val address = intent.extras?.get("address") as Address
            val isChecked = intent.extras?.get("isChecked") as Boolean
            val position = intent.extras?.get("position") as Int?
            if (list == null) {
                //list?.add(address)
                list = arrayOf(address)
                changeFragment()
            } else {
                val fragment = supportFragmentManager.findFragmentByTag("display_address_fragment") as DisplayAddressFragment
                fragment.updateList(address, isChecked, position)
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        isRunning = true
        if (showFragment != null) {
            showFragment?.invoke()
            showFragment = null
        }
    }

    override fun onStop() {
        super.onStop()
        isRunning = false
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
                //changeFragment()
                showFragment = ::changeFragment
                if (isRunning) {
                    showFragment?.invoke()
                    showFragment = null
                }
            }

            override fun onFailure(call: Call<Array<Address>>?, t: Throwable?) {
                Toast.makeText(this@BaseActivity, "error occcurred", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * This function will make the activity change a fragment depending upon whether the list is empty or not
     */

    private fun changeFragment() = with(supportFragmentManager.beginTransaction()) {
        //replace(R.id.address_display_fragment_container, BlankAddressFragment(), "blank_address_fragment")
        if (list?.isEmpty() == true) {
            replace(R.id.address_display_fragment_container, BlankAddressFragment(), "blank_address_fragment")
        } else {
            val bundle = Bundle().apply { putSerializable("addresses", list) }
            val displayAddressFragment = DisplayAddressFragment().apply { arguments = bundle }
            replace(R.id.address_display_fragment_container, displayAddressFragment, "display_address_fragment")
        }
        commit()
        Unit
    }

    /**
     * It is a callback method that the DisplayAddressFragment will call if on deletion of an element the list becomes empty
     */

    override fun notifyListIsEmpty() {

        list = null
        with(supportFragmentManager.beginTransaction()) {
            replace(R.id.address_display_fragment_container, BlankAddressFragment())
            commit()
        }
    }
}