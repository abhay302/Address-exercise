package com.example.abhay.address.controllers.display

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.abhay.address.R
import com.example.abhay.address.controllers.change.AddOrEditAddressActivity
import kotlinx.android.synthetic.main.fragment_empty_address.view.*

/**
 * This fragment will be displayed when no address is present in the list of addresses.
 */
class EmptyAddressFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_empty_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.add_address_button.setOnClickListener {
            startActivity(Intent(activity, AddOrEditAddressActivity::class.java))
        }
    }
}