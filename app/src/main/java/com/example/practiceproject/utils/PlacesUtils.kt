package com.example.practiceproject.utils

import androidx.lifecycle.LiveData
import com.example.practiceproject.data.places.Place

interface PlacesUtils {
    fun getAll(): List<Place>

    fun addAll(data: LiveData<List<List<Place>>>)
}