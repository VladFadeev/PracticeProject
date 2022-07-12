package com.example.practiceproject

import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.Throws
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Used singleton pattern
 */
object MetroService {
    private const val BASE_URL = "https://my-json-server.typicode.com/BeeWhy/metro/"

    fun addMetroMarkers(map: GoogleMap) {
        val stations = stations
        stations.enqueue(object : Callback<List<Station>?> {
            override fun onResponse(
                call: Call<List<Station>?>,
                response: Response<List<Station>?>
            ) {
                if (response.body() != null) {
                    for (station in response.body()!!) {
                        if (station.name.lowercase(Locale.getDefault()) != "error") {
                            map.addMarker(
                                MarkerOptions()
                                    .position(
                                        LatLng(
                                            station.latitude.toDouble(),
                                            station.longitude.toDouble()
                                        )
                                    )
                                    .title(station.name)
                            )
                        }
                    }
                }
            }

            override fun onFailure(
                call: Call<List<Station>?>,
                t: Throwable
            ) {
                Log.e(MetroService::class.java.simpleName, "Response fail", t)
            }
        })
    }

    fun addStationsList(layout : LinearLayout, behavior : BottomSheetBehavior<LinearLayout>) {
        stations.enqueue(object : Callback<List<Station>?> {
            override fun onResponse(
                call: Call<List<Station>?>,
                response: Response<List<Station>?>
            ) {
                if (response.body() != null) {
                    for (station in response.body()!!) {
                        if (station.name.lowercase(Locale.getDefault()) != "error") {
                            val textView = TextView(layout.context)
                            textView.text = station.name
                            textView.setOnClickListener { view: View ->
                                if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED
                                    && (view as TextView).text == textView.text
                                ) {
                                    behavior.state = BottomSheetBehavior.STATE_HIDDEN
                                } else {
                                    behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                                }
                            }
                            layout.addView(textView)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Station>?>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    @get:Throws(SecurityException::class)
    private val stations: Call<List<Station>>
        get() = if (PermissionsUtils.isInternetPermissionGranted) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val metroAPI = retrofit.create(MetroAPI::class.java)
            metroAPI.stations()
        } else {
            throw SecurityException()
        }
}