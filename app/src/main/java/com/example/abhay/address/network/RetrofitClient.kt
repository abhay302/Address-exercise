package com.example.abhay.address.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * This class will create a retrofit client which will hit the API.
 */
object RetrofitClient {
    val client by lazy {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        val builder = Retrofit.Builder()
                .baseUrl("https://shop-spree.herokuapp.com/api/ams/user/addresses/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
        val retrofit = builder.build()

        retrofit.create(Client::class.java)
    }
}