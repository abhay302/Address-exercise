package com.example.abhay.address

import retrofit2.Call
import retrofit2.http.*

/**
 * This interface defines various operations performed on the API.
 */
interface Client {

    @GET(" ")
    fun getAllAddresses(@Query("token") token: String = "52e04d83e87e509f07982e6ac851e2d2c67d1d0eabc4fe78"): Call<Array<Address>>

    @POST(" ")
    fun createAddress(@Body addresses: Address): Call<Address>

    @DELETE("{id}")
    fun deleteAddress(@Path("id") id: Int, @Query("token") token: String = "52e04d83e87e509f07982e6ac851e2d2c67d1d0eabc4fe78"): Call<DeleteActionReply>

    @PUT("{id}")
    fun updateAddress(@Path("id") id: Int, @Body requestToken: Address): Call<Address>
}