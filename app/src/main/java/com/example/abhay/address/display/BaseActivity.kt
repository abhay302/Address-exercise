package com.example.abhay.address.display

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.abhay.address.R
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
    var list: Array<Address>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        fetchData()
        //Toast.makeText(this, "Inside onCreate", Toast.LENGTH_SHORT).show()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //fetchData()
        if (intent?.extras?.get("address") != null) {
            val address = intent.extras?.get("address") as Address
            val isChecked = intent.extras?.get("isChecked") as Boolean
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show()

            /*if(supportFragmentManager.findFragmentByTag("display_address_fragment") as? DisplayAddressFragment == null) {
                changeFragment()
            }*/
            val fragment = supportFragmentManager.findFragmentByTag("display_address_fragment") as DisplayAddressFragment
            fragment.updateList(address, isChecked)
        }
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
                changeFragment()
            }

            override fun onFailure(call: Call<Array<Address>>?, t: Throwable?) {
                Toast.makeText(this@BaseActivity, "error occcurred", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * This function will make the activity change a fragment depending upon whether the list is empty or not
     */
    private fun changeFragment() {
        val transaction = supportFragmentManager.beginTransaction()

        if (list?.isEmpty() == true) {
            transaction.replace(R.id.address_display_fragment_container, BlankAddressFragment(), "blank_address_fragment")
        } else {
            val bundle = Bundle().apply { putSerializable("addresses", list) }
            val displayAddressFragment = DisplayAddressFragment().apply { arguments = bundle }
            transaction.replace(R.id.address_display_fragment_container, displayAddressFragment, "display_address_fragment")
        }

        //  transaction.addToBackStack(null)
        transaction.commit()
    }

    /**
     * It is a callback method that the DisplayAddressFragment will call if on deletion of an element the list becomes empty
     */
    override fun notifyListIsEmpty() {
        /*val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.address_display_fragment_container, BlankAddressFragment())
        transaction.commit()*/

        with(supportFragmentManager.beginTransaction()) {
            replace(R.id.address_display_fragment_container, BlankAddressFragment())
            commit()
        }
    }

}