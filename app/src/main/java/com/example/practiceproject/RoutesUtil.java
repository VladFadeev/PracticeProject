package com.example.practiceproject;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Used Bill Pugh implementation of the singleton pattern
 */
public class RoutesUtil {
    private final static String ORIGIN = "origin=";
    private final static String LOCATION_SEPARATOR = ",";
    private final static String DEST = "destination=";
    private final static String SENSOR_STATE = "sensor=false";
    private final static String MODE = "mode=";
    private final static String PARAMETERS_SEPARATOR = "&";
    private final static String KEY = "key=";
    private final static String MAPS_KEY_API = "AIzaSyCkZ5C_lE__uqk0ENbixLk9-e7wd42qHUc";
    private final static String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?";

    private RoutesUtil() {}

    private final static class InstanceHolder {
        static final RoutesUtil INSTANCE = new RoutesUtil();
    }

    public static RoutesUtil getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public PolylineOptions drawRoute(LatLng origin, LatLng dest, String mode) {
        PolylineOptions polylineOptions = null;
        ExecutorService service = Executors.newFixedThreadPool(2);
        try {
            Future<String> data = service.submit((Callable<String>) () -> {
                String url = getDirectionsUrl(origin, dest, mode);
                return downloadUrl(url);
            });
            polylineOptions = service.submit((Callable<PolylineOptions>) () -> {
                JSONObject jObject;
                List<List<HashMap<String, String>>> routes;
                PolylineOptions lineOptions = null;
                try {
                    jObject = new JSONObject(data.get());
                    DirectionsJSONParser parser = new DirectionsJSONParser();

                    routes = parser.parse(jObject);
                    ArrayList<LatLng> points;
                    for (int i = 0; i < routes.size(); i++) {
                        points = new ArrayList<>();
                        lineOptions = new PolylineOptions();

                        List<HashMap<String, String>> path = routes.get(i);

                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);

                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);

                            points.add(position);
                        }

                        lineOptions.addAll(points);
                        lineOptions.width(12);
                        lineOptions.color(0xFF000000);
                        lineOptions.geodesic(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return lineOptions;
            }).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            service.shutdown();
        }
        return polylineOptions;
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest, String modeState) {
        StringBuilder sb = new StringBuilder(BASE_URL);
        sb.append(ORIGIN).append(origin.latitude).append(LOCATION_SEPARATOR)
                .append(origin.longitude).append(PARAMETERS_SEPARATOR)
                .append(DEST).append(dest.latitude).append(LOCATION_SEPARATOR)
                .append(dest.longitude).append(PARAMETERS_SEPARATOR)
                .append(SENSOR_STATE).append(PARAMETERS_SEPARATOR)
                .append(MODE).append(modeState).append(PARAMETERS_SEPARATOR)
                .append(KEY).append(MAPS_KEY_API);
        return sb.toString();
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            if (iStream != null) {
                iStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return data;
    }
}
