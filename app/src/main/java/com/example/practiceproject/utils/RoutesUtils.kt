package com.example.practiceproject.utils

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.model.DirectionsResult
import com.google.maps.GeoApiContext
import com.google.maps.DirectionsApi
import com.google.maps.model.TravelMode
import java.util.ArrayList
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

/**
 * Used singleton pattern
 */
object RoutesUtils {

    fun drawRoute(orig: LatLng, dest: LatLng, MAPS_KEY_API: String?): PolylineOptions? {
        var result: PolylineOptions? = null
        val service = Executors.newFixedThreadPool(2)
        val res = service.submit(
            Callable {
                val context = GeoApiContext.Builder().apiKey(MAPS_KEY_API).build()
                val directionsApiRequest = DirectionsApi.newRequest(context)
                directionsApiRequest.origin(
                    com.google.maps.model.LatLng(
                        orig.latitude,
                        orig.longitude
                    )
                ).destination(com.google.maps.model.LatLng(dest.latitude, dest.longitude))
                    .mode(TravelMode.DRIVING).await()
            } as Callable<DirectionsResult>)
        val optionsFuture = service.submit(
            Callable {
                val options = PolylineOptions()
                for (route in res.get().routes) {
                    val latLngList: MutableList<LatLng> = ArrayList()
                    for (latLng in route.overviewPolyline.decodePath()) {
                        latLngList.add(LatLng(latLng.lat, latLng.lng))
                    }
                    options.addAll(latLngList)
                    options.width(12f)
                    options.color(-0x1000000)
                    options.geodesic(true)
                }
                options
            } as Callable<PolylineOptions>)
        try {
            result = optionsFuture.get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } finally {
            service.shutdown()
        }
        return result
    }
}