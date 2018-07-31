package com.example.abhay.address


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * This fragment is used to display a list of addresses.
 * Implements interface containing a callback method, which is called when a menu icon in a particular entry is clicked in the recycler view
 * indicating the fragment to open a popup menu.
 */
class DisplayAddressFragment : Fragment(), AddressAdapter.ShowPopupCallback {

    /**
     * This interface will be implemented by the hosting activity
     * Provides a callback method which tells the activity to change the fragment since, the list is now empty after deletion of the last element.
     */
    interface EmptyListCallback {
        fun notifyListIsEmpty()
    }

    lateinit var list: MutableList<Address>
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_display_address, container, false)
    }

    override fun onResume() {
        super.onResume()
        addAddressButtonClickListener()
        populateList()
    }

    /**
     * The function will put the entries in the recycler view using the array of address received from the hosting activity.
     */
    private fun populateList() {
        list = (arguments?.get("addresses") as Array<Address>).toMutableList()
        Toast.makeText(activity, list.size.toString(), Toast.LENGTH_SHORT).show()

        recyclerView = activity!!.findViewById(R.id.display_address_recycler_view)
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
                    updateAddress(address)
                    true
                }
                else -> TODO("Error occurred")
            }

        }
    }

    /**
     * This function is used to update a particular address
     * It will send the requested object to be updated to the AddOrEditAddressActivity which will handle the rest of the work
     */
    private fun updateAddress(address: Address) {
        val bundle = Bundle().apply { putSerializable("address", address) }
        val intent = Intent(activity, AddOrEditAddressActivity::class.java).apply {
            putExtra("address", bundle)
        }
        startActivity(intent)
    }

    /**
     * This function is used to delete a particular address
     */
    private fun deleteAddress(address: Address, position: Int) {

        fun sendDeleteRequest(id: Int, position: Int) {
            val call = RetrofitClient.client.deleteAddress(id)

            call.enqueue(object : Callback<DeleteActionReply> {

                override fun onResponse(call: Call<DeleteActionReply>?, response: Response<DeleteActionReply>?) {
                    if (response?.code() == 200) {
                        Toast.makeText(activity, response.body()?.message
                                ?: "Some acknowledgement message", Toast.LENGTH_LONG).show()
                        list.removeAt(position)
                        if (list.isEmpty()) {
                            (activity as BaseActivity).notifyListIsEmpty()   // Tells the hosting activity to change the fragment since, the list is now empty.
                        }
                        recyclerView.adapter?.notifyDataSetChanged()
                    } else {
                        Toast.makeText(activity, response?.body()?.errors
                                ?: "Problem in deletion", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<DeleteActionReply>?, t: Throwable?) {
                    Toast.makeText(activity, "Error occurred", Toast.LENGTH_LONG).show()
                }

            })
        }

        /*createRetrofitClient()
        sendDeleteRequest(address.id!!, position)*/

        val builder = AlertDialog.Builder(activity)
        builder.setMessage("Do you really want to delete this address?")
                .setTitle("Alert!!!")
                .setCancelable(true)
                .setPositiveButton("Yes") { dialogInterface, i ->
                    sendDeleteRequest(address.id!!, position)

                    dialogInterface.cancel()

                }.setNegativeButton("No") { dialogInterface, i ->
                    dialogInterface.cancel()
                }
        val dialog = builder.create()
        dialog.show()
    }

    /**
     * This function will be called when the user clicks the add_address_button
     */
    private fun addAddressButtonClickListener() {
        activity?.findViewById<FloatingActionButton>(R.id.add_address_button_in_display_address_fragment)?.setOnClickListener {
            startActivity(Intent(activity, AddOrEditAddressActivity::class.java))
        }
    }
}

/**
 * It is an Adapter class which is used to put items in the Recycler view
 */
class AddressAdapter(val list: MutableList<Address>, val fragment: DisplayAddressFragment) : RecyclerView.Adapter<AddressAdapter.AddressHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_custom_layout, parent, false)
        return AddressHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: AddressHolder, position: Int) {
        val address = list[position]

        holder.textView.text = (address.address1?.takeIf { it.isNotBlank() }?.plus(", ") ?: "")
                .plus(address.address2?.takeIf { it.isNotBlank() }?.plus(", ") ?: "")
                .plus(address.city?.takeIf { it.isNotBlank() }?.plus(", ") ?: "")
                .plus(address.zipcode ?: "")

        if (address.id == getDefaultAddress()) {
            holder.checkBox.isChecked = true
            Log.d(position.toString(), getDefaultAddress().toString())
        } else {
            holder.checkBox.isChecked = false
        }

        holder.imageView.setOnClickListener {
            Log.d(position.toString(), "I was clicked")     // It was for testing
            fragment.showPopup(address, position, holder.imageView)
        }
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

    /**
     * The will return the id of the default address (if present) else, it returns a very small number
     */
    private fun getDefaultAddress(): Int {
        val sharedPreferences = fragment.activity?.getSharedPreferences("defaultAddress", Context.MODE_PRIVATE)
        return sharedPreferences?.getInt("id", Int.MIN_VALUE)!!
    }
}
