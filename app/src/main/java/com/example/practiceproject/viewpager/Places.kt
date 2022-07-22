package com.example.practiceproject.viewpager

import androidx.lifecycle.LiveData

enum class Places(private val utils: PlacesUtils) {
    Stations(StationsUtils);//,
    //Sights(SightsUtils);

    fun getAll(data: LiveData<List<List<Place>>>) = utils.getAll(data)

    fun addAll(data: LiveData<List<List<Place>>>) = utils.addAll(data)
}