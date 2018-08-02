package com.example.abhay.address

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

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
        //fetchData()
        //Toast.makeText(this, "Inside onCreate", Toast.LENGTH_SHORT).show()
        if (intent?.extras?.get("addresses") != null) {
            list = intent.extras?.get("addresses") as Array<Address>
            changeFragment()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //fetchData()

        if (intent?.extras?.get("address") != null) {
            val address = intent.extras?.get("address") as Address
            val isChecked = intent.extras?.get("isChecked") as Boolean
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show()

            val fragment = supportFragmentManager.findFragmentByTag("display_address_fragment") as DisplayAddressFragment
            fragment.updateList(address, isChecked)
        }
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