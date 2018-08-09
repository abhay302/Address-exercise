package com.example.abhay.address.network

import com.example.abhay.address.data.Address
import com.example.abhay.address.data.DeleteActionReply
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.*

const val TOKEN = "52e04d83e87e509f07982e6ac851e2d2c67d1d0eabc4fe78"

/**
 * This interface defines various operations performed on the API.
 */
interface Client {

    @GET(" ")
    fun getAllAddresses(@Query("token") token: String = TOKEN): Call<Array<Address>>

    @POST(" ")
    fun createAddress(@Body addresses: Address, @Query("token") token: String = TOKEN): Call<JsonElement>

    @DELETE("{id}")
    fun deleteAddress(@Path("id") id: Int, @Query("token") token: String = TOKEN): Call<DeleteActionReply>

    @PUT("{id}")
    fun updateAddress(@Path("id") id: Int, @Body requestToken: Address, @Query("token") token: String = TOKEN): Call<JsonElement>
}