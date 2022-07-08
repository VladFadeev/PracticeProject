package com.example.practiceproject;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Used Bill Pugh implementation of the singleton pattern
 */
public class RoutesUtils {

    private RoutesUtils() {}

    private final static class InstanceHolder {
        static final RoutesUtils INSTANCE = new RoutesUtils();
    }

    public static RoutesUtils getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public PolylineOptions drawRoute(LatLng orig, LatLng dest, String MAPS_KEY_API) {
        PolylineOptions result = null;
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<DirectionsResult> res = service.submit((Callable<DirectionsResult>) () -> {
            GeoApiContext context = new GeoApiContext.Builder().apiKey(MAPS_KEY_API).build();
            DirectionsApiRequest directionsApiRequest = DirectionsApi.newRequest(context);
            return directionsApiRequest.origin(new com.google.maps.model.LatLng(orig.latitude, orig.longitude)).
                    destination(new com.google.maps.model.LatLng(dest.latitude, dest.longitude)).mode(TravelMode.DRIVING).await();
        });
        Future<PolylineOptions> optionsFuture = service.submit((Callable<PolylineOptions>) () -> {
            PolylineOptions options = new PolylineOptions();
            for (DirectionsRoute route : res.get().routes) {
                List<LatLng> latLngList = new ArrayList<>();
                for (com.google.maps.model.LatLng latLng : route.overviewPolyline.decodePath()) {
                    latLngList.add(new LatLng(latLng.lat, latLng.lng));
                }
                options.addAll(latLngList);
                options.width(12);
                options.color(0xFF000000);
                options.geodesic(true);
            }
            return options;
        });
        try {
            result = optionsFuture.get();
        } catch (InterruptedException | ExecutionException e ) {
            e.printStackTrace();
        } finally {
            service.shutdown();
        }
        return result;
    }
}
