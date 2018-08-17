package com.example.abhay.address.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * The members of this class will represent the possible fields in the address detail
 */
data class Address(

        @SerializedName("id")
        var id: Int? = null,

        @SerializedName("firstname")
        var firstname: String? = null,

        @SerializedName("lastname")
        var lastname: String? = null,

        @SerializedName("address1")
        var address1: String? = null,

        @SerializedName("address2")
        var address2: String? = null,

        @SerializedName("city")
        var city: String? = null,

        @SerializedName("zipcode")
        var zipcode: String? = null,

        @SerializedName("phone")
        var phone: String? = null,

        @SerializedName("alternative_phone")
        var alternativePhone: String? = null,

        @SerializedName("company")
        var company: String? = null,

        @SerializedName("state_id")
        var stateId: Int? = null,

        @SerializedName("country_id")
        var countryId: Int? = null

) : Serializable {
    companion object {
        /**
         * will contain the list of addresses
         */
        lateinit var list: MutableList<Address>
    }
}