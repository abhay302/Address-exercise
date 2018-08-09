package com.example.abhay.address.adapters

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.example.abhay.address.R
import com.example.abhay.address.data.Address
import com.example.abhay.address.fragments.DisplayAddressFragment
import com.example.abhay.address.fragments.getDefaultAddress
import com.example.abhay.address.fragments.posCurrentDefaultAddress

/**
 * It is an Adapter class which is used to put items in the Recycler view
 */
class AddressAdapter(val list: MutableList<Address>, val fragment: DisplayAddressFragment) : RecyclerView.Adapter<AddressAdapter.AddressHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_custom_layout, parent, false)
        //Log.d("viewHolderCreated", "Inside onCreateViewHolder")
        return AddressHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: AddressHolder, position: Int) {
        val address = list[position]

        holder.textView.text = (address.address1?.takeIf { it.isNotBlank() }?.plus(", ") ?: "")
                //holder.textView.text = (address.id.toString().plus(", "))
                .plus(address.address2?.takeIf { it.isNotBlank() }?.plus(", ") ?: "").plus("\n")
                .plus(address.city?.takeIf { it.isNotBlank() }?.plus(". ") ?: "").plus("\n")
                .plus(address.zipcode ?: "")

        if (address.id == getDefaultAddress(fragment.context!!)) {
            holder.checkBox.isChecked = true
            posCurrentDefaultAddress = holder.adapterPosition
            //holder.checkBox.setHasTransientState(true)
            Log.d(position.toString(), getDefaultAddress(fragment.context!!).toString())
        } else {
            holder.checkBox.isChecked = false
        }

        holder.imageView.setOnClickListener {
            Log.d(position.toString(), "menu icon clicked")     // It was for testing
            //fragment.showPopup(address, position, holder.imageView)
            fragment.showPopup(address, holder.adapterPosition, holder.imageView)
        }

        Log.d("Inside onBindViewHolder", position.toString())
    }

    class AddressHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView = view.findViewById<TextView>(R.id.address_detail)
        val checkBox = view.findViewById<CheckBox>(R.id.default_address_indicator)
        val imageView = view.findViewById<ImageView>(R.id.menu_selector)
    }

    /**
     * It is used to notify a parent fragment that a menu icon is clicked
     */
    interface ShowPopupCallback {
        fun showPopup(address: Address, position: Int, view: View)
    }
}