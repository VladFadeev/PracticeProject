package com.example.practiceproject.data.places

import androidx.lifecycle.LiveData
import com.example.practiceproject.data.places.Place
import com.example.practiceproject.utils.PlacesUtils
import com.example.practiceproject.utils.SightsUtils
import com.example.practiceproject.utils.StationsUtils

enum class PlaceType(private val utils: PlacesUtils) {
    Station(StationsUtils),
    Sight(SightsUtils);

    fun getAll() = utils.getAll()

    fun addAll(data: LiveData<List<List<Place>>>) = utils.addAll(data)
}