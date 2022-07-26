package com.example.practiceproject.utils

import com.example.practiceproject.data.places.Station
import retrofit2.http.GET
import retrofit2.Call

interface MetroAPI {

    @GET("stations")
    fun stations(): Call<List<Station>>
}