package com.example.practiceproject.viewpager

import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import com.example.practiceproject.PermissionsUtils
import com.example.practiceproject.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object StationsUtils: PlacesUtils {
    private const val BASE_URL = "https://my-json-server.typicode.com/BeeWhy/metro/"

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
            textView.text = station.stationName
            textView.setOnClickListener {
                if (behavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                    behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                peek.text = (it as TextView).text
            }
            layout.addView(textView)

        }
    }

    override fun addAll(model: PlacesViewModel): List<Place> {
        return getAll(model)
    }

    @Throws(SecurityException::class)
    override fun getAll(model: PlacesViewModel): List<Station> {
        var list: List<Station> = ArrayList()
        if (PermissionsUtils.isInternetPermissionGranted) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val metroAPI = retrofit.create(MetroAPI::class.java)
            metroAPI.stations().enqueue(object : Callback<List<Station>> {
                override fun onResponse(
                    call: Call<List<Station>>,
                    response: Response<List<Station>>
                ) {
                    if (response.body() != null) {
                        (model.placesList.value as MutableList).add(Places.Stations.ordinal, response.body()!!
                            .filter { station -> station.stationName.lowercase() != "error" })
                    }
                }

                override fun onFailure(call: Call<List<Station>>, t: Throwable) {
                    Log.e(PlacesViewModel::class.java.simpleName, t.stackTraceToString())
                }
            })
        } else {
            throw SecurityException("Don't have permission")
        }
        return list
    }

    @Throws(SecurityException::class)
    fun getStations(model: PlacesViewModel, num : Int) {
        if (PermissionsUtils.isInternetPermissionGranted) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val metroAPI = retrofit.create(MetroAPI::class.java)
            metroAPI.stations().enqueue(object : Callback<List<Station>> {
                override fun onResponse(
                    call: Call<List<Station>>,
                    response: Response<List<Station>>
                ) {
                    if (response.body() != null) {
                        (model.placesList.value as MutableList).add(response.body()!!
                            .filter { station -> station.stationName.lowercase() != "error" }.subList(0, num))
                    }
                }

                override fun onFailure(call: Call<List<Station>>, t: Throwable) {
                    Log.e(PlacesViewModel::class.java.simpleName, t.stackTraceToString())
                }
            })
        } else {
            throw SecurityException("Don't have permission")
        }
    }
}
