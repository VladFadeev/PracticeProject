package com.example.practiceproject.utils

import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.example.practiceproject.R
import com.example.practiceproject.dao
import com.example.practiceproject.data.places.Place
import com.example.practiceproject.data.places.PlaceType
import com.example.practiceproject.data.places.Station
import com.example.practiceproject.ui.MainActivity
import com.example.practiceproject.ui.navController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors

const val DEST_KEY = "dest"

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
            val textView = TextView(layout.context, null, 0, R.style.ListItem)
            textView.text = station.getName()
            val ll = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            ll.setMargins(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    10f,
                    layout.resources.displayMetrics
                ).toInt(),
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    15f,
                    layout.resources.displayMetrics
                ).toInt(),
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    10f,
                    layout.resources.displayMetrics
                ).toInt(),
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    5f,
                    layout.resources.displayMetrics
                ).toInt()
            )
            textView.layoutParams = ll
            textView.setOnClickListener {
                if (behavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                    behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                } else if (peek.text == (it as TextView).text) {
                    behavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
                if ((it as TextView).text != peek.text) {
                    peek.text = it.text
                }
            }
            layout.addView(textView)
        }
        val routeAction = bottomSheet.findViewById<Button>(R.id.action1)
        routeAction.setOnClickListener{
            val station = stations.filter { it.getName() == peek.text }[0]
            val args = bundleOf(DEST_KEY to arrayOf(station.getLatitude(), station.getLongitude()).toFloatArray())
            navController.navigate(R.id.menu_map, args)
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
                    .filter { station -> station.getName().lowercase() != "error" }
                result.stream().forEach { it.setType(PlaceType.Station) }
            }
        } else {
            throw SecurityException("Don't have permission")
        }
        return result!!
    }
}
