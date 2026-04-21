package com.example.planerpodrozy.api

import retrofit2.http.Query
import com.example.planerpodrozy.model.PlaceResponse
import retrofit2.http.GET

interface GeoapifyApi {

    @GET("v1/geocode/autocomplete")
    suspend fun autocomplete(
        @Query("text") text: String,
        @Query("apiKey") apiKey: String,
        @Query("limit") limit: Int = 5,
        @Query("lang") lang: String = "pl"
    ): PlaceResponse
}