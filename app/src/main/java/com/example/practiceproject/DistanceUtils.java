package com.example.practiceproject;

import com.google.maps.DirectionsApi;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

import java.time.Instant;

/**
 * Used Bill Pugh implementation of the singleton pattern
 */
public class DistanceUtils {

    private DistanceUtils() {}

    private static final class InstanceHolder {
        final static DistanceUtils INSTANCE = new DistanceUtils();
    }

    public static DistanceUtils getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public DistanceMatrix estimateRouteTime(Instant time, String MAPS_API_KEY, Boolean isForCalculateArrivalTime, DirectionsApi.RouteRestriction routeRestriction, LatLng departure, LatLng... arrivals) {
        try {
            GeoApiContext context = new GeoApiContext.Builder().apiKey(MAPS_API_KEY).build();
            DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(context);
            if (isForCalculateArrivalTime) {
                req.departureTime(time);
            } else {
                req.arrivalTime(time);
            }
            /*if (routeRestriction == null) {
                routeRestriction = DirectionsApi.RouteRestriction.TOLLS;
            }*/
            return req.origins(departure)
                    .destinations(arrivals)
                    .mode(TravelMode.DRIVING)
                    //.avoid(routeRestriction)
                    .language("ru")
                    .await();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
