package com.example.practiceproject.utils

import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LiveData
import com.example.practiceproject.R
import com.example.practiceproject.dao
import com.example.practiceproject.data.places.Place
import com.example.practiceproject.data.places.PlaceType
import com.example.practiceproject.data.places.Station
import com.google.android.material.bottomsheet.BottomSheetBehavior
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors

object StationsUtils: PlacesUtils {
    private val BASE_URL = "https://my-json-server.typicode.com/BeeWhy/metro/"

    /*fun addStationsMarkers(map: GoogleMap, stations: List<Station>) {

        /*for (station in stationList) {
            map.addMarker(
                MarkerOptions()
                    .position(LatLng(station.latitude.toDouble(), station.longitude.toDouble()))
                    .title(station.name)
            )
        }*/
    }*/

    fun addStationsList(layout: LinearLayout, bottomSheet: LinearLayout, stations: List<Station>) {
        layout.removeAllViews()
        val behavior = BottomSheetBehavior.from(bottomSheet)
        val peek = bottomSheet.findViewById<TextView>(R.id.bottom_sheet_peek)
        for (station in stations) {
            val textView = TextView(layout.context)
            textView.text = station.name
            textView.setOnClickListener {
                if (behavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                    behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                peek.text = (it as TextView).text
            }
            layout.addView(textView)
        }
    }

    fun ReceiveStations() {
        val service = Executors.newSingleThreadExecutor()
        service.submit { dao.insertAll(getAllStations()) }
        service.shutdown()
    }

    override fun addAll(data: LiveData<List<List<Place>>>) {
        getAll()
    }

    @Throws(SecurityException::class)
    override fun getAll(): List<Place> {
        //val res = dao.getAll()

        /*if (data.value != null) {
            (data.value as MutableList).add(PlaceType.Station.ordinal, res)
        } else {
            (data as MutableLiveData).postValue(ArrayList<List<Place>>().apply {
                add(PlaceType.Station.ordinal, res)
            })
        }*/
        return dao.getAllStations()
    }

    @Throws(SecurityException::class)
    private fun getAllStations(): List<Station> {
        var result: List<Station>? = null
        if (PermissionsUtils.isInternetPermissionGranted) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val metroAPI = retrofit.create(MetroAPI::class.java)
            val response = metroAPI.stations().execute()
            if (response.body() != null) {
                result = response.body()!!
                    .filter { station -> station.name.lowercase() != "error" }
                result.stream().forEach { it.setType(PlaceType.Station) }
            }
        } else {
            throw SecurityException("Don't have permission")
        }
        return result!!
    }
}
