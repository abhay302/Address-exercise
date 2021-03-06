package com.example.abhay.address.viewhelpers

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.example.abhay.address.R
import com.example.abhay.address.controllers.display.AddressListFragment
import com.example.abhay.address.controllers.display.getDefaultAddress
import com.example.abhay.address.controllers.display.posCurrentDefaultAddress
import com.example.abhay.address.models.Address
import kotlinx.android.synthetic.main.list_item_custom_layout.view.*

/**
 * It is an Adapter class for the Recycler view
 */
class AddressAdapter(private val list: MutableList<Address>, private val fragment: AddressListFragment) : RecyclerView.Adapter<AddressAdapter.AddressHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_custom_layout, parent, false)
        return AddressHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: AddressHolder, position: Int) {
        val address = list[position]

        holder.textView.text = (address.address1?.takeIf { it.isNotBlank() }?.plus(", ") ?: "")
                .plus(address.address2?.takeIf { it.isNotBlank() }?.plus(", ") ?: "").plus("\n")
                .plus(address.city?.takeIf { it.isNotBlank() }?.plus(". ") ?: "").plus("\n")
                .plus(address.zipcode ?: "")

        if (address.id == getDefaultAddress(fragment.context!!)) {
            holder.checkBox.isChecked = true
            posCurrentDefaultAddress = holder.adapterPosition
            Log.d(position.toString(), getDefaultAddress(fragment.context!!).toString())
        } else {
            holder.checkBox.isChecked = false
        }

        holder.imageView.setOnClickListener {
            Log.d(position.toString(), "menu icon clicked")
            fragment.showPopup(address, holder.adapterPosition, holder.imageView)
        }

        Log.d("Inside onBindViewHolder", position.toString())
    }

    class AddressHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.address_detail
        val checkBox: CheckBox = view.default_address_indicator
        val imageView: ImageView = view.menu_icon
    }

    /**
     * It is used to notify the AddressListFragment that a menu icon is clicked
     */
    interface ShowPopupCallback {
        fun showPopup(address: Address, position: Int, view: View)
    }
}