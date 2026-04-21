package com.example.planerpodrozy.api

import com.example.planerpodrozy.api.GeoapifyApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://api.geoapify.com/"

    val api: GeoapifyApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeoapifyApi::class.java)
    }
}