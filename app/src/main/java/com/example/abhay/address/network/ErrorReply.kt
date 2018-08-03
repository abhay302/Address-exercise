package com.example.abhay.address.network

import com.google.gson.annotations.SerializedName

data class ErrorReply(

        @SerializedName("exception")
        var exception: String? = null,

        @SerializedName("error")
        var error: String? = null,

        @SerializedName("errors")
        var errors: Errors? = null) {

    data class Errors(

            /* @SerializedName("phone")
             var phone: Array<String>? = null,*/

            @SerializedName("address1")
            var address1: Array<String>? = null,

            @SerializedName("city")
            var city: Array<String>? = null,

            @SerializedName("zipcode")
            var zipcode: Array<String>? = null,

            @SerializedName("state")
            var stateId: Array<String>? = null
    )
}