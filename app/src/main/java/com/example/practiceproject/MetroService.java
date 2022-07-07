package com.example.practiceproject;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Used Bill Pugh implementation of the singleton pattern
 */
public class MetroService {

    private static final String BASE_URL = "https://my-json-server.typicode.com/BeeWhy/metro/";

    private MetroService() {
    }

    private static final class InstanceHolder {
        static final MetroService INSTANCE = new MetroService();
    }

    public static MetroService getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void addMetroMarkers(GoogleMap map) {
        Call<List<Station>> stations = getStations();
        stations.enqueue(new Callback<List<Station>>() {
            @Override
            public void onResponse(@NonNull Call<List<Station>> call,
                                   @NonNull Response<List<Station>> response) {
                if (response.body() != null) {
                    for (Station station : response.body()) {
                        if (!Objects.equals(station.getName().toLowerCase(), "error")) {
                            map.addMarker(new MarkerOptions()
                                    .position(new LatLng(station.getLatitude(), station.getLongitude()))
                                    .title(station.getName()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Station>> call,
                                  @NonNull Throwable t) {
                Log.e(MetroService.class.getSimpleName(), "Response fail", t);
            }
        });
    }

    private Call<List<Station>> getStations() throws SecurityException{
        if (PermissionsUtils.isInternetPermissionGranted()) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MetroAPI metroAPI = retrofit.create(MetroAPI.class);
            return metroAPI.stations();
        } else {
            throw new SecurityException();
        }
    }
}
