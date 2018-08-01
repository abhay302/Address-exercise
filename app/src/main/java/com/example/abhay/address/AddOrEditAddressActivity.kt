package com.example.abhay.address

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * This class is used to add and update a particular address record.
 */
class AddOrEditAddressActivity : AppCompatActivity() {

    lateinit var requestObject: Address

    /**
     * will indicate whether the requested operation is an UPDATE query
     */
    var isUpdateQuery = false

    /**
     * will contain address ID in case it is an update query
     */
    var id = Int.MIN_VALUE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_or_edit_address)

        if (intent.extras != null) {    // whether the intended query is an update request or not
            isUpdateQuery = true
            inializeForm()
            title = "Update Address"
        } else {
            initializeFormTestingPurpose()  // for testing purpose
            title = "Add Address"
        }
        //Toast.makeText(this, getDefaultAddress().toString(), Toast.LENGTH_LONG).show()
    }

    /**
     * This function is used to initialize the input fields if the requested operation is UPDATE record.
     */
    private fun inializeForm() {
        val address = (intent.extras["address"] as Bundle)["address"] as Address
        //Toast.makeText(this, address.toString(), Toast.LENGTH_LONG).show()
        findViewById<EditText>(R.id.input_Name).setText((address.firstname
                ?: "").plus(" ").plus((address.lastname ?: "")))
        findViewById<EditText>(R.id.input_Address1).setText(address.address1 ?: "")
        findViewById<EditText>(R.id.input_Address2).setText(address.address2 ?: "")
        findViewById<EditText>(R.id.input_City).setText(address.city ?: "")
        findViewById<EditText>(R.id.input_State).setText(address.stateId?.toString() ?: "")
        findViewById<EditText>(R.id.input_Zipcode).setText(address.zipcode ?: "")
        id = address.id!!
        if (id == getDefaultAddress()) {
            val apply = findViewById<CheckBox>(R.id.checkBox_Make_Default_Address).apply {
                isChecked = true
                isClickable = false
            }
        }
    }

    /**
     * This function was only created for testing.
     */
    private fun initializeFormTestingPurpose() {
        findViewById<EditText>(R.id.input_Name).setText("My name")
        findViewById<EditText>(R.id.input_Address1).setText("My address1")
        findViewById<EditText>(R.id.input_Address2).setText("My address2")
        findViewById<EditText>(R.id.input_City).setText("In my city")
        findViewById<EditText>(R.id.input_State).setText("1400")
        findViewById<EditText>(R.id.input_Zipcode).setText("284128")
    }

    /**
     * This function will create and initialize the request object which is used to send data to the server.
     */
    private fun createRequestObject() {
        requestObject = Address().apply {
            val name = findViewById<EditText>(R.id.input_Name).text.toString().trim().split(" ")
            firstname = name[0]
            lastname = if (name.size > 1) name[1] else null
            address1 = findViewById<EditText>(R.id.input_Address1).text.toString().trim()
            address2 = findViewById<EditText>(R.id.input_Address2).text.toString().trim() +
                    findViewById<EditText>(R.id.input_Landmark).text.toString().trim()
            city = findViewById<EditText>(R.id.input_City).text.toString().trim()
            stateId = findViewById<EditText>(R.id.input_State).text.toString().trim().takeIf { it.isNotEmpty() }?.toInt()
            zipcode = findViewById<EditText>(R.id.input_Zipcode).text.toString().trim()

            countryId = 105
            phone = (1234567890).toString()
            token = "52e04d83e87e509f07982e6ac851e2d2c67d1d0eabc4fe78"

            //sendPostRequest()
        }
        Log.d("requestObject", requestObject.toString())
    }

    /**
     * This function will be called when the requested operation is a POST operation i.e. create address.
     * It will generate POST query
     */
    private fun sendPostRequest() {

        /*val call = client.createAddress(RequestToken("52e04d83e87e509f07982e6ac851e2d2c67d1d0eabc4fe78",
                "Shuklan", "Hapur", 105, 1400, 245304, 98969))
*/
        val call = RetrofitClient.client.createAddress(requestObject)

        call.enqueue(object : Callback<Address> {

            override fun onResponse(call: Call<Address>?, response: Response<Address>?) {
                if (response?.code() == 200) {
                    //Toast.makeText(this@AddOrEditAddressActivity, "Success", Toast.LENGTH_LONG).show()

                    /*if (this@AddOrEditAddressActivity.findViewById<CheckBox>(R.id.checkBox_Make_Default_Address).isChecked) {
                        setDefaultAddress(response.body()?.id!!)
                    }*/

                    startActivity(Intent(this@AddOrEditAddressActivity, BaseActivity::class.java).apply {
                        putExtra("address", response.body())
                        putExtra("isChecked", this@AddOrEditAddressActivity.findViewById<CheckBox>(R.id.checkBox_Make_Default_Address).isChecked)
                    })
                    this@AddOrEditAddressActivity.finish()
                } else {
                    Toast.makeText(this@AddOrEditAddressActivity, "Invalid details", Toast.LENGTH_LONG).show()

                }
            }

            override fun onFailure(call: Call<Address>?, t: Throwable?) {
                Toast.makeText(this@AddOrEditAddressActivity, "Error occurred", Toast.LENGTH_LONG).show()
            }

        })
    }

    /**
     * This function will be called when the requested operation is a PUT operation i.e. update address.
     * It will generate a PUT query
     */
    private fun sendPutRequest() {
        val call = RetrofitClient.client.updateAddress(id, requestObject)

        call.enqueue(object : Callback<Address> {

            override fun onResponse(call: Call<Address>?, response: Response<Address>?) {
                if (response?.code() == 200) {
                    //Toast.makeText(this@AddOrEditAddressActivity, "Successfully updated address", Toast.LENGTH_LONG).show()
                    /*if (this@AddOrEditAddressActivity.findViewById<CheckBox>(R.id.checkBox_Make_Default_Address).isChecked) {
                        setDefaultAddress(response.body()?.id!!)
                    }*/

                    startActivity(Intent(this@AddOrEditAddressActivity, BaseActivity::class.java).apply {
                        putExtra("address", response.body())
                        putExtra("isChecked", this@AddOrEditAddressActivity.findViewById<CheckBox>(R.id.checkBox_Make_Default_Address).isChecked)
                    })
                    this@AddOrEditAddressActivity.finish()
                } else {
                    Toast.makeText(this@AddOrEditAddressActivity, "Invalid details", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Address>?, t: Throwable?) {
                Toast.makeText(this@AddOrEditAddressActivity, "Error occurred", Toast.LENGTH_LONG).show()
            }

        })
    }

    /**
     * This function is used the save the default address locally if the checkbox indicates that the address is default or not is marked checked
     * Store the id as a shared preference.
     */
    private fun setDefaultAddress(id: Int) {
        val sharedPreferences = getSharedPreferences("defaultAddress", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("id", id)
            commit()
        }
    }

    /**
     * Returns id of the default address
     */
    private fun getDefaultAddress(): Int {
        val sharedPreferences = getSharedPreferences("defaultAddress", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("id", Int.MIN_VALUE)
    }

    /**
     * This function will be called when the user clicks a submit button.
     */
    fun sendData(view: View) {
        createRequestObject()
        if (isUpdateQuery) {
            sendPutRequest()
        } else {
            sendPostRequest()
        }
    }
}