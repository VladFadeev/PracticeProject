package com.example.practiceproject.data.places

import androidx.lifecycle.LiveData
import java.util.concurrent.Callable
import java.util.concurrent.Executors

object PlacesModel {
    fun getAllPlaces(): List<List<Place>> {
        val result: MutableList<List<Place>> = ArrayList()
        for (place in PlaceType.values()){
                val service = Executors.newSingleThreadExecutor()
                val res = service.submit(Callable {place.getAll()}).get()
                service.shutdown()
                result.add(res)
        }
        return result
    }

    fun updateAllPlaces(data: LiveData<List<List<Place>>>) {
        for (place in PlaceType.values()) {
            place.addAll(data)
        }
    }
}