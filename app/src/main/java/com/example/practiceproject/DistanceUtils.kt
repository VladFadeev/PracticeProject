package com.example.practiceproject

import com.google.maps.DirectionsApi.RouteRestriction
import com.google.maps.model.DistanceMatrix
import com.google.maps.GeoApiContext
import com.google.maps.DistanceMatrixApi
import com.google.maps.model.LatLng
import com.google.maps.model.TravelMode
import java.lang.Exception
import java.time.Instant

/**
 * Used singleton pattern
 */
object DistanceUtils {

    fun estimateRouteTime(
        time: Instant?, MAPS_API_KEY: String?,
        isForCalculateArrivalTime: Boolean,
        routeRestriction: RouteRestriction?, departure: LatLng?,
        vararg arrivals: LatLng?
    ): DistanceMatrix? {
        try {
            val context = GeoApiContext.Builder().apiKey(MAPS_API_KEY).build()
            val req = DistanceMatrixApi.newRequest(context)
            if (isForCalculateArrivalTime) {
                req.departureTime(time)
            } else {
                req.arrivalTime(time)
            }
            /*if (routeRestriction == null) {
                routeRestriction = DirectionsApi.RouteRestriction.TOLLS;
            }*/return req.origins(departure)
                .destinations(*arrivals)
                .mode(TravelMode.DRIVING) //.avoid(routeRestriction)
                .language("ru")
                .await()
        } catch (e: Exception) {
            println(e.message)
        }
        return null
    }
}