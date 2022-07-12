package com.example.practiceproject

import retrofit2.http.GET
import retrofit2.Call

interface MetroAPI {

    @GET("stations")
    fun stations(): Call<List<Station>>
}