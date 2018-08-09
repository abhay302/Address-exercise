package com.example.abhay.address.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.example.abhay.address.R
import com.example.abhay.address.data.Address
import com.example.abhay.address.data.ErrorReply
import com.example.abhay.address.network.RetrofitClient
import com.google.gson.Gson
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern


/**
 * This class is used to add and update a particular address record.
 */
class AddOrEditAddressActivity : AppCompatActivity() {

    private var startBaseActivity: (() -> Unit)? = null
    private var isRunning: Boolean = true

    lateinit var requestObject: Address

    /**
     * will indicate whether the requested operation is an UPDATE query
     */
    var isUpdateQuery = false

    /**
     * will contain address ID in case it is an update query
     */
    var id = Int.MIN_VALUE

    /**
     * will contain the position of the element to be updated (if it is an update query)
     */
    var position: Int? = null

    lateinit var call: Call<JsonElement>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_or_edit_address)

        val toolbar = findViewById<android.support.v7.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        title = ""
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }

        setImageButtonClickListener()
        toolbar.findViewById<TextView>(R.id.title).text = if (intent.extras != null) {    // whether the intended query is an update request or not
            isUpdateQuery = true
            inializeForm()
            position = (intent.extras["address"] as Bundle)["position"] as Int
            "Update Address"

        } else {
            //initializeFormTestingPurpose()  // for testing purpose
            "Add Address"
        }
        addTextChangeListener(R.id.input_Address1, R.id.input_Address1_Container)
        addTextChangeListener(R.id.input_State, R.id.input_State_Container)
        addTextChangeListener(R.id.input_City, R.id.input_City_Container)
        addTextChangeListener(R.id.input_Zipcode, R.id.input_Zipcode_Container)
    }

    override fun onRestart() {
        super.onRestart()
        isRunning = true
        if (startBaseActivity != null)
            startBaseActivity?.invoke()
    }

    override fun onStop() {
        super.onStop()
        isRunning = false
    }


    /**
     * will register the click listener for the send button
     */
    private fun setImageButtonClickListener() {
        findViewById<ImageButton>(R.id.send_button).setOnClickListener {
            removeErrorFields()
            createRequestObject()
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
            call = if (isUpdateQuery) {
                RetrofitClient.client.updateAddress(id, requestObject)
            } else {
                RetrofitClient.client.createAddress(requestObject)
            }
            sendRequest()
            it.isClickable = false
        }
    }

    /**
     * This function will create and initialize the request object which is used to send data to the server.
     */
    private fun createRequestObject() {
        requestObject = Address().apply {
            val name = findViewById<EditText>(R.id.input_Name).text.toString().trim().split(Pattern.compile(" "), 2)
            firstname = name[0]
            lastname = if (name.size > 1) name[1] else null
            address1 = findViewById<EditText>(R.id.input_Address1).text.toString().trim()
            address2 = findViewById<EditText>(R.id.input_Address2).text.toString().trim() +
                    findViewById<EditText>(R.id.input_Landmark).text.toString().trim()
            city = findViewById<EditText>(R.id.input_City).text.toString().trim()
            stateId = findViewById<EditText>(R.id.input_State).text.toString().trim().takeIf { it.isNotEmpty() }?.run {
                val temp = filter { it.isDigit() }
                if (length == temp.length)
                    toInt()
                else
                    Int.MIN_VALUE
            }
            zipcode = findViewById<EditText>(R.id.input_Zipcode).text.toString().trim()

            countryId = 105
            phone = (1234567890).toString()

        }
        Log.d("requestObject", requestObject.toString())
    }

    /**
     * This function will be send the post/put request depending upon the call object
     */
    private fun sendRequest() {

        call.enqueue(object : Callback<JsonElement> {

            override fun onResponse(call: Call<JsonElement>?, response: Response<JsonElement>?) {
                if (response?.code() == 200) {
                    val address = Gson().fromJson(response.body().toString(), Address::class.java)

                    startBaseActivity = {
                        startActivity(Intent(this@AddOrEditAddressActivity, BaseActivity::class.java).apply {
                            putExtra("address", address)
                            putExtra("isChecked", this@AddOrEditAddressActivity.findViewById<CheckBox>(R.id.checkBox_Make_Default_Address).isChecked)
                            putExtra("position", position)
                        })
                        this@AddOrEditAddressActivity.finish()
                    }
                    if (isRunning) {
                        startBaseActivity?.invoke()
                    }
                } else if (response?.code() == 422) {
                    val error = Gson().fromJson(response.errorBody()?.string(), ErrorReply::class.java)
                    setErrorFields(error.errors)
                } else if (response?.code() == 404) {
                    Toast.makeText(this@AddOrEditAddressActivity, "Address not found", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@AddOrEditAddressActivity, "Error occurred", Toast.LENGTH_SHORT).show()
                }
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                findViewById<ImageButton>(R.id.send_button).isClickable = true
            }

            override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                Toast.makeText(this@AddOrEditAddressActivity, "Error occurred", Toast.LENGTH_SHORT).show()
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                findViewById<ImageButton>(R.id.send_button).isClickable = true
            }

        })
    }

    /**
     * This will set the error fields for the input fields which has some error
     */
    private fun setErrorFields(errors: ErrorReply.Errors?) {

        if (errors?.city != null)
            findViewById<TextInputLayout>(R.id.input_City_Container).error = errors.city?.get(0)
        if (errors?.address1 != null)
            findViewById<TextInputLayout>(R.id.input_Address1_Container).error = errors.address1?.get(0)
        if (errors?.stateId != null)
            findViewById<TextInputLayout>(R.id.input_State_Container).error = errors.stateId?.get(0)
        if (errors?.zipcode != null)
            findViewById<TextInputLayout>(R.id.input_Zipcode_Container).error = errors.zipcode?.get(0)
    }

    /**
     * This will remove error fields from all the possibly erroneous fields before sending request to the server
     */
    private fun removeErrorFields() {

        findViewById<TextInputLayout>(R.id.input_City_Container).error = null
        findViewById<TextInputLayout>(R.id.input_Address1_Container).error = null
        findViewById<TextInputLayout>(R.id.input_State_Container).error = null
        findViewById<TextInputLayout>(R.id.input_Zipcode_Container).error = null
    }

    /**
     * Returns id of the default address
     */
    private fun getDefaultAddress(): Int {
        val sharedPreferences = getSharedPreferences("defaultAddress", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("id", Int.MIN_VALUE)
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
            findViewById<CheckBox>(R.id.checkBox_Make_Default_Address).apply {
                isChecked = true
                isClickable = false
            }
        }
    }

    fun addTextChangeListener(editTextId: Int, containerId: Int) {
        findViewById<EditText>(editTextId).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                findViewById<TextInputLayout>(containerId).error = null
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    /**
     * This function was only created for testing.
     */
    private fun initializeFormTestingPurpose() {
        findViewById<EditText>(R.id.input_Name).setText("My name")
        //findViewById<EditText>(R.id.input_Address1).setText((Math.random()*1000).toInt().toString())
        findViewById<EditText>(R.id.input_Address1).setText("My address1")
        findViewById<EditText>(R.id.input_Address2).setText("My address2")
        findViewById<EditText>(R.id.input_City).setText("In my city")
        findViewById<EditText>(R.id.input_State).setText("1400")
        findViewById<EditText>(R.id.input_Zipcode).setText("284128")
    }
}