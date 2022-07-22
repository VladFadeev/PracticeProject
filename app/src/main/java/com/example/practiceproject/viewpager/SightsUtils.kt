package com.example.practiceproject.viewpager

import androidx.lifecycle.LiveData

object SightsUtils: PlacesUtils {
    override fun getAll(data: LiveData<List<List<Place>>>) {
        TODO("JSON and DB access")
    }

    override fun addAll(data: LiveData<List<List<Place>>>) {
        return getAll(data)
    }
}