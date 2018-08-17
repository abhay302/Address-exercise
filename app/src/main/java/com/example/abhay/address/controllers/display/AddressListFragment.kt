package com.example.abhay.address.controllers.display

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import com.example.abhay.address.R
import com.example.abhay.address.api.RetrofitClient
import com.example.abhay.address.controllers.change.AddOrEditAddressActivity
import com.example.abhay.address.models.Address
import com.example.abhay.address.models.DeleteActionReply
import com.example.abhay.address.viewhelpers.AddressAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_address_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Will show the position of current default address(if present) in the address list otherwise, null.
 */
var posCurrentDefaultAddress: Int? = null

/**
 * This fragment is used to display a list of addresses.
 * Implements interface containing a callback method, which is called when a menu icon in a particular entry is clicked in the recycler view
 * indicating the fragment to open a popup menu.
 */
class AddressListFragment : Fragment(), AddressAdapter.ShowPopupCallback {

    /**
     * This interface will be implemented by the hosting activity
     * Provides a callback method which tells the activity to change the fragment since, the list is now empty after deletion of the last element.
     */
    interface EmptyListCallback {
        fun notifyListIsEmpty()
    }

    /**
     * will hold the reference to the address list
     */
    private lateinit var list: MutableList<Address>

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_address_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addAddressButtonClickListener()
        populateList()
    }

    /**
     * Will update the address list when an item is added or updated
     */
    fun updateList(address: Address, isChecked: Boolean, position: Int?) {
        Log.d("address received:", address.toString())
        if (isChecked) {
            if (posCurrentDefaultAddress != null) {
                recyclerView.adapter.notifyItemChanged(posCurrentDefaultAddress!!)
            }
            setDefaultAddress(context!!, address.id!!)
        }
        if (position == null) {                 // It means that address is not present in the list i.e. create query
            list.add(0, address)
            recyclerView.adapter.notifyItemInserted(0)
            recyclerView.layoutManager.scrollToPosition(0)
        } else {                                // It means that the address is already present in the list i.e. update query
            list[position] = address
            recyclerView.adapter.notifyItemChanged(position)
        }
    }

    /**
     * The function will put the entries in the recycler view using the address list.
     */
    private fun populateList() {
        list = Address.list

        recyclerView = activity!!.display_address_recycler_view
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = AddressAdapter(list, this)

        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))  // add divider
    }

    /**
     * The function will display a popup menu which contains two items:
     * 1. Edit address
     * 2. Remove address
     */
    override fun showPopup(address: Address, position: Int, view: View) {
        val popupMenu = PopupMenu(activity, view)
        val menuInflater = popupMenu.menuInflater
        menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.remove_address -> {
                    deleteAddress(address, position)
                    true
                }
                R.id.edit_address -> {
                    updateAddress(address, position)
                    true
                }
                else -> {
                    showPopup(address, position, view)
                    true
                }
            }

        }
    }

    /**
     * This function is used to update a particular address
     * It will send the requested object to be updated to the AddOrEditAddressActivity which will handle the rest of the work
     */
    private fun updateAddress(address: Address, position: Int) {
        val bundle = Bundle().apply {
            putSerializable("address", address)
            putInt("position", position)
        }
        val intent = Intent(activity, AddOrEditAddressActivity::class.java).apply {
            putExtra("address", bundle)
        }
        activity?.startActivityForResult(intent, ADD_OR_EDIT_ADDRESS_REQUEST_CODE)
    }

    /**
     * This function is used to delete a particular address
     */
    private fun deleteAddress(address: Address, position: Int) {

        val builder = AlertDialog.Builder(activity)
        builder.setMessage(getString(R.string.delete_address_alert_dialog_message))
                .setTitle(getString(R.string.delete_address_alert_dialog_title))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.delete_address_alert_dialog_pos_button)) { dialogInterface, _ ->
                    Log.d(position.toString(), "delete request at")
                    sendDeleteRequest(address.id!!, position)

                    dialogInterface.cancel()

                }.setNegativeButton(getString(R.string.delete_address_alert_dialog_neg_button)) { dialogInterface, _ ->
                    dialogInterface.cancel()
                }
        val dialog = builder.create()
        dialog.show()
    }

    private fun sendDeleteRequest(id: Int, position: Int) {
        val holder = recyclerView.findViewHolderForAdapterPosition(position) as AddressAdapter.AddressHolder
        holder.imageView.isClickable = false
        activity?.progressBar?.visibility = View.VISIBLE


        val call = RetrofitClient.client.deleteAddress(id)

        call.enqueue(object : Callback<DeleteActionReply> {

            override fun onResponse(call: Call<DeleteActionReply>?, response: Response<DeleteActionReply>?) {
                if (response?.code() == 200) {

                    list.removeAt(position)
                    if (list.isEmpty()) {
                        (activity as AddressListDisplayActivity).notifyListIsEmpty()   // Tells the hosting activity to change the fragment since, the list is now empty.
                    }
                    recyclerView.adapter.notifyItemRemoved(position)

                    if (position == posCurrentDefaultAddress) {
                        posCurrentDefaultAddress = null
                        setDefaultAddress(context!!, Int.MIN_VALUE)
                    }
                } else {
                    val error = Gson().fromJson(response?.errorBody()?.string(), DeleteActionReply::class.java)
                    Toast.makeText(activity, error.errors
                            ?: getString(R.string.retrofit_delete_action_default_failure_message), Toast.LENGTH_LONG).show()
                }
                holder.imageView.isClickable = true
                activity?.progressBar?.visibility = View.GONE
            }

            override fun onFailure(call: Call<DeleteActionReply>?, t: Throwable?) {
                Toast.makeText(activity, getString(R.string.retrofit_default_failure_message), Toast.LENGTH_LONG).show()
                holder.imageView.isClickable = true
                activity?.progressBar?.visibility = View.GONE
            }

        })
    }

    /**
     * This function will be called when the user clicks the add_address_button
     */
    private fun addAddressButtonClickListener() {
        activity?.add_address_button?.setOnClickListener {
            activity?.startActivityForResult(Intent(activity, AddOrEditAddressActivity::class.java), ADD_OR_EDIT_ADDRESS_REQUEST_CODE)
        }
    }
}

/**
 * The will return the id of the default address (if present) else, it returns a very small number
 */
fun getDefaultAddress(context: Context): Int {
    val sharedPreferences = context.getSharedPreferences("defaultAddress", Context.MODE_PRIVATE)
    return sharedPreferences?.getInt("id", Int.MIN_VALUE)!!
}


/**
 * This function is used the save the default address locally if the checkbox indicates that the address is default or not is marked checked
 * Store the id as a shared preference.
 */
fun setDefaultAddress(context: Context, id: Int) {
    val sharedPreferences = context.getSharedPreferences("defaultAddress", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putInt("id", id)
        commit()
    }
}