package com.example.abhay.address.controllers.change

import android.app.Activity
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
import com.example.abhay.address.api.RetrofitClient
import com.example.abhay.address.controllers.display.AddressListDisplayActivity
import com.example.abhay.address.models.Address
import com.example.abhay.address.models.ErrorReply
import com.example.abhay.address.models.Errors
import com.google.gson.Gson
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.regex.Pattern


/**
 * This class is used to add and update a particular address record.
 */
class AddOrEditAddressActivity : AppCompatActivity() {

    /**
     * indicates whether the current activity is in running state
     */
    private var isRunning: Boolean = true

    /**
     * it will hold the address details sent to the server in a create/update address query
     */
    private lateinit var requestObject: Address

    /**
     * will indicate whether the requested operation is an UPDATE query
     */
    private var isUpdateQuery = false

    /**
     * will contain address ID in case it is an update query
     */
    var id = Int.MIN_VALUE

    /**
     * will contain the position of the element to be updated (if it is an update query)
     */
    var position: Int? = null

    private lateinit var call: Call<JsonElement>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_or_edit_address)

        val toolbar = findViewById<android.support.v7.widget.Toolbar>(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        title = null
        toolbar.findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }

        setImageButtonClickListener()
        toolbar.findViewById<TextView>(R.id.title).text = if (intent.extras != null) {    // whether the intended query is an update request or not
            isUpdateQuery = true
            inializeForm()
            position = (intent.extras["address"] as Bundle)["position"] as Int
            getString(R.string.update_address_title)

        } else {
            getString(R.string.add_address_title)
        }
        addTextChangeListener(R.id.input_Address1, R.id.input_Address1_Container)
        addTextChangeListener(R.id.input_State, R.id.input_State_Container)
        addTextChangeListener(R.id.input_City, R.id.input_City_Container)
        addTextChangeListener(R.id.input_Zipcode, R.id.input_Zipcode_Container)
    }

    override fun onRestart() {
        super.onRestart()
        isRunning = true
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
            if (validateInput()) {
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
            address2 = (findViewById<EditText>(R.id.input_Address2).text.toString().trim() + " " +
                    findViewById<EditText>(R.id.input_Landmark).text.toString().trim()).trim()
            city = findViewById<EditText>(R.id.input_City).text.toString().trim()
            stateId = findViewById<EditText>(R.id.input_State).text.toString().trim().takeIf { it.isNotEmpty() }?.toInt()
            zipcode = findViewById<EditText>(R.id.input_Zipcode).text.toString().trim()

            countryId = 105
            phone = getString(R.string.default_phone_number)

        }
        Log.d("requestObject", requestObject.toString())
    }

    /**
     * This function will be send the post/put request depending upon the call object
     */
    private fun sendRequest() {

        call.enqueue(object : Callback<JsonElement> {

            override fun onResponse(call: Call<JsonElement>?, response: Response<JsonElement>?) {
                when (response?.code()) {
                    200 -> {
                        val address = Gson().fromJson(response.body().toString(), Address::class.java)

                        Timer().schedule(object : TimerTask() {
                            override fun run() {
                                if (isRunning) {
                                    cancel()
                                    setResult(Activity.RESULT_OK,
                                            Intent(this@AddOrEditAddressActivity, AddressListDisplayActivity::class.java).apply {
                                                putExtra("address", address)
                                                putExtra("isChecked", this@AddOrEditAddressActivity.findViewById<CheckBox>(R.id.checkBox_Make_Default_Address).isChecked)
                                                putExtra("position", position)
                                            })
                                    this@AddOrEditAddressActivity.finish()
                                }
                            }
                        }, 0, 1)
                    }
                    422 -> {
                        val error = Gson().fromJson(response.errorBody()?.string(), ErrorReply::class.java)
                        setErrorFields(error.errors)
                    }
                    404 -> Toast.makeText(this@AddOrEditAddressActivity, getString(R.string.retrofit_default_error404_message), Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(this@AddOrEditAddressActivity, getString(R.string.retrofit_error_message), Toast.LENGTH_SHORT).show()
                }
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                findViewById<ImageButton>(R.id.send_button).isClickable = true
            }

            override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                Toast.makeText(this@AddOrEditAddressActivity, getString(R.string.retrofit_default_failure_message), Toast.LENGTH_SHORT).show()
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                findViewById<ImageButton>(R.id.send_button).isClickable = true
            }

        })
    }

    /**
     * This will set the error fields for the input fields which has some error
     */
    private fun setErrorFields(errors: Errors?) {

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

    /**
     * register a text change listener with the input fields
     * will hide the errors if user types in the erroneous fields
     */
    private fun addTextChangeListener(editTextId: Int, containerId: Int) {
        findViewById<EditText>(editTextId).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                findViewById<TextInputLayout>(containerId).error = null
            }
        })
    }

    private fun validateInput(): Boolean {
        /**
         * initially assume that all the given input fields are error free
         */
        var isErrorFree = true

        val errors = Errors()

        val errorMessages = mutableListOf<String>()         // created mutable list to support more than one validation messages for an input field
        findViewById<EditText>(R.id.input_Address1).text.toString().apply {
            if (isBlank())
                errorMessages.add(getString(R.string.input_default_empty_field_error_message))
            if (errorMessages.isNotEmpty()) {
                errors.address1 = errorMessages.toTypedArray()
                isErrorFree = false
            }
            errorMessages.clear()
        }
        findViewById<EditText>(R.id.input_City).text.toString().apply {
            if (isBlank())
                errorMessages.add(getString(R.string.input_default_empty_field_error_message))
            if (errorMessages.isNotEmpty()) {
                errors.city = errorMessages.toTypedArray()
                isErrorFree = false
            }
            errorMessages.clear()
        }
        findViewById<EditText>(R.id.input_State).text.toString().apply {
            if (isBlank())
                errorMessages.add(getString(R.string.input_default_empty_field_error_message))
            if (any { !it.isDigit() })
                errorMessages.add(getString(R.string.input_state_not_digit_error_message))
            if (errorMessages.isNotEmpty()) {
                errors.stateId = errorMessages.toTypedArray()
                isErrorFree = false
            }
            errorMessages.clear()
        }
        findViewById<EditText>(R.id.input_Zipcode).text.toString().apply {
            if (isBlank())
                errorMessages.add(getString(R.string.input_default_empty_field_error_message))
            if (errorMessages.isNotEmpty()) {
                errors.zipcode = errorMessages.toTypedArray()
                isErrorFree = false
            }
            errorMessages.clear()
        }
        if (!isErrorFree)
            setErrorFields(errors)
        return isErrorFree
    }
}