package com.example.abhay.address.models

import com.google.gson.annotations.SerializedName

/**
 * The members of this class will represent the possible error replies from the server in-case of create/update query
 */
data class ErrorReply(

        @SerializedName("error")
        var error: String? = null,

        @SerializedName("errors")
        var errors: Errors? = null
)

data class Errors(

        @SerializedName("address1")
        var address1: Array<String>? = null,

        @SerializedName("city")
        var city: Array<String>? = null,

        @SerializedName("zipcode")
        var zipcode: Array<String>? = null,

        @SerializedName("state")
        var stateId: Array<String>? = null
)