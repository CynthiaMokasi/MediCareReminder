
package com.example.medicareReminder.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL =
        "https://medicarereminder-cynthia-api20260911112209-hgcpgrbdf9g6h5d6.switzerlandnorth-01.azurewebsites.net/"

    val apiService: ApiService by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
