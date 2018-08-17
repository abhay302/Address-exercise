package com.example.abhay.address.controllers.display

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.abhay.address.R
import com.example.abhay.address.api.RetrofitClient
import com.example.abhay.address.models.Address
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

const val ADD_OR_EDIT_ADDRESS_REQUEST_CODE = 250

/**
 * This activity will display a list of addresses
 * It can display two types of fragments:
 * 1. EmptyAddressFragment: If no address is present
 * 2. AddressListFragment: Will display a list of addresses in this fragment
 */

class AddressListDisplayActivity : AppCompatActivity(), AddressListFragment.EmptyListCallback {

    /**
     * Will indicate whether the current activity is in resumed state
     */
    private var isRunning: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list_display)
        fetchData()
        val toolbar = findViewById<android.support.v7.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        title = null
        toolbar.findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }
        toolbar.findViewById<TextView>(R.id.title).text = getString(R.string.address_list_display_activity_title)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == ADD_OR_EDIT_ADDRESS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (intent?.extras?.get("address") != null) {
                val address = intent.extras?.get("address") as Address
                val isChecked = intent.extras?.get("isChecked") as Boolean
                val position = intent.extras?.get("position") as Int?
                if (Address.list.isEmpty()) {
                    with(supportFragmentManager.beginTransaction()) {
                        replace(R.id.address_display_fragment_container, AddressListFragment(), "display_address_fragment")
                        commit()
                    }
                }
                supportFragmentManager.executePendingTransactions()
                val fragment = supportFragmentManager.findFragmentByTag("display_address_fragment") as AddressListFragment
                fragment.updateList(address, isChecked, position)
            }
        }
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
     * This function fetches the addresses from the server using Retrofit
     */
    private fun fetchData() {
        val call = RetrofitClient.client.getAllAddresses()

        call.enqueue(object : Callback<Array<Address>> {

            override fun onResponse(call: Call<Array<Address>>?, response: Response<Array<Address>>?) {
                Log.d("received", response.toString())
                Log.d("data", response?.body().toString())
                Address.list = response?.body()?.toMutableList() ?: mutableListOf()

                Address.list.indices.forEach {
                    Log.d(it.toString(), Address.list[it].toString())
                }
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        if (isRunning) {
                            changeFragment()
                            cancel()
                        }
                    }
                }, 0, 1)
            }

            override fun onFailure(call: Call<Array<Address>>?, t: Throwable?) {
                Toast.makeText(this@AddressListDisplayActivity, getString(R.string.retrofit_default_failure_message), Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * This function will make the activity change a fragment depending upon whether the list is empty or not
     */
    private fun changeFragment() = with(supportFragmentManager.beginTransaction()) {
        if (Address.list.isEmpty())
            add(R.id.address_display_fragment_container, EmptyAddressFragment(), "blank_address_fragment")
        else
            add(R.id.address_display_fragment_container, AddressListFragment(), "display_address_fragment")

        commit()
        Unit
    }

    /**
     * It is a callback method that the AddressListFragment will call if on deletion of an element the list becomes empty
     */
    override fun notifyListIsEmpty() {

        with(supportFragmentManager.beginTransaction()) {
            replace(R.id.address_display_fragment_container, EmptyAddressFragment(), "blank_address_fragment")
            commit()
        }
    }
}