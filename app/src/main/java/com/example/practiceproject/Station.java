package com.example.practiceproject;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Station {
    private final String name;
    private final float latitude;
    private final float longitude;

    public Station(String name, float latitude, float longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Float.compare(station.latitude, latitude) == 0 && Float.compare(station.longitude, longitude) == 0 && Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, latitude, longitude);
    }

    @NonNull
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Station{");
        sb.append("name='").append(name).append('\'');
        sb.append(", latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append('}');
        return sb.toString();
    }
}
