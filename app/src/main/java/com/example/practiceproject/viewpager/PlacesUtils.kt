package com.example.practiceproject.viewpager

import androidx.lifecycle.LiveData

interface PlacesUtils {
    fun getAll(data: LiveData<List<List<Place>>>)

    fun addAll(data: LiveData<List<List<Place>>>)
}