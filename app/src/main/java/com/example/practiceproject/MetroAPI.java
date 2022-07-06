package com.example.practiceproject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MetroAPI {

    @GET("stations")
    Call<List<Station>> stations();
}
