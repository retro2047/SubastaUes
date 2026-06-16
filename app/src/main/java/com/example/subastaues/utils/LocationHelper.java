package com.example.subastaues.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationHelper {

    public interface LocationCallback {
        void onLocationFound(Location location);
        void onLocationError(String error);
    }

    private final Context context;
    private final LocationManager locationManager;

    public LocationHelper(Context context) {
        this.context = context.getApplicationContext();
        this.locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
    }

    public boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isGpsEnabled() {
        if (locationManager == null) return false;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation(LocationCallback callback) {
        if (!hasLocationPermission()) {
            callback.onLocationError("Location permission not granted");
            return;
        }

        if (!isGpsEnabled()) {
            callback.onLocationError("GPS/Location is disabled");
            return;
        }

        if (locationManager == null) {
            callback.onLocationError("Location Manager is null");
            return;
        }

        Location gpsLoc = null;
        Location netLoc = null;

        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                gpsLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        } catch (Exception ignored) {}

        try {
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                netLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        } catch (Exception ignored) {}

        Location bestLocation = null;
        if (gpsLoc != null && netLoc != null) {
            bestLocation = (gpsLoc.getTime() > netLoc.getTime()) ? gpsLoc : netLoc;
        } else if (gpsLoc != null) {
            bestLocation = gpsLoc;
        } else if (netLoc != null) {
            bestLocation = netLoc;
        }

        if (bestLocation != null) {
            callback.onLocationFound(bestLocation);
            return;
        }

        // Request single update if no last known location
        try {
            String provider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ? 
                    LocationManager.NETWORK_PROVIDER : LocationManager.GPS_PROVIDER;
            
            locationManager.requestSingleUpdate(provider, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        callback.onLocationFound(location);
                    } else {
                        callback.onLocationError("Could not retrieve location");
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider) {}

                @Override
                public void onProviderDisabled(String provider) {}
            }, null);
        } catch (Exception e) {
            callback.onLocationError("Error requesting location: " + e.getMessage());
        }
    }

    public String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i));
                    if (i < address.getMaxAddressLineIndex()) sb.append(", ");
                }
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown Location";
    }
}
