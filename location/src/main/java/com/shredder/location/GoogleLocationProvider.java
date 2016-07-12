package com.shredder.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import lombok.Getter;

public class GoogleLocationProvider {

    public interface OnLocationChangedListener {
        void onLocationChanged(Location result);
    }

    private static final String TAG = "GoogleLocationProvider";
    private final GoogleApiClient mGoogleApiClient;

    private LocationConfig config;
    private boolean connectedToApiService = false;
    private OnLocationChangedListener listener;
    @Getter
    private Location lastKnownLocation;
    private final LocationReportingAdjuster locationReportingAdjuster;

    public GoogleLocationProvider(Context context) {
        config = new LocationConfig() {
            @Override
            public LocationAccuracy getAccuracy() {
                return LocationAccuracy.High;
            }
        };

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Location Services", "Must have basic location permissions to run.");
            mGoogleApiClient = null;
        } else {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(createConnectionCallbacks())
                    .addOnConnectionFailedListener(createConnectionFailedListener())
                    .build();
        }
        locationReportingAdjuster = new LocationReportingAdjuster();
    }

    @NonNull
    private GoogleApiClient.OnConnectionFailedListener createConnectionFailedListener() {
        return new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                Log.d(TAG, "onConnectionFailed");
                connectedToApiService = false;
            }
        };
    }

    @NonNull
    private GoogleApiClient.ConnectionCallbacks createConnectionCallbacks() {
        return new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Log.d(TAG, "onConnected");
                connectedToApiService = true;
                if (listener != null) {
                    startLocationUpdates();
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d(TAG, "onConnectionSuspended");
                connectedToApiService = false;
            }
        };
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            newLocationArrivedFromGoogleApi(location);
        }
    };

    private void newLocationArrivedFromGoogleApi(Location location) {
        Log.d(TAG, "onLocationChanged");
        lastKnownLocation = location;
        notifyListeners(location);
        if (locationReportingAdjuster.shouldChange(location)) {
            startLocationUpdates();
        }
    }

    public void addLocationListener(OnLocationChangedListener listener) {
        Log.d(TAG, "addLocationListener");
        tryToStartLocationUpdates();
        this.listener = listener;
    }

    private void tryToStartLocationUpdates() {
        if (connectedToApiService) {
            startLocationUpdates();
        } else {
            Log.d(TAG, "connecting");
            mGoogleApiClient.connect();
        }
    }

    public void removeLocationListener() {
        Log.d(TAG, "removeLocationListener");
        listener = null;
        if (!connectedToApiService) {
            return;
        }
        stopLocationUpdates();
        disconnect();
    }

    private void disconnect() {
        Log.d(TAG, "disconnecting");
        connectedToApiService = false;
        mGoogleApiClient.disconnect();
    }

    LocationCallback callback = new LocationCallback() {
        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            Log.d(TAG, "onLocationAvailability");
        }

        @Override
        public void onLocationResult(LocationResult result) {
            super.onLocationResult(result);
            Log.d(TAG, "onLocationResult");
        }
    };

    private void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates");
        LocationRequest request = new LocationRequestFactory().createRequest(config, locationReportingAdjuster.isMovingFast());
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, locationListener);
        Location immediateLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (immediateLocation == null) {
            return;
        }
        newLocationArrivedFromGoogleApi(immediateLocation);
    }

    private void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, callback);
    }

    private void notifyListeners(Location location) {
        if (listener != null) {
            listener.onLocationChanged(location);
        }
    }

    public void setConfig(LocationConfig config) {
        this.config = config;
        tryToStartLocationUpdates();
    }
}