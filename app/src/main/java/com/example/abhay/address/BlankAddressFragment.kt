package com.example.abhay.address

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast

/**
 * This fragment will be displayed when no address is present in the list of addresses.
 */
class BlankAddressFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addAddressButton = view.findViewById<FloatingActionButton>(R.id.add_address_button)

        addAddressButton.setOnClickListener {
            //Toast.makeText(context, "Button is clicked", Toast.LENGTH_SHORT).show()
            startActivity(Intent(activity, AddOrEditAddressActivity::class.java))
        }
    }
}
