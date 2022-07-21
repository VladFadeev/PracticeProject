package com.example.practiceproject.viewpager

interface PlacesUtils {
    fun getAll(model: PlacesViewModel): List<Place>

    fun addAll(model: PlacesViewModel): List<Place>
}