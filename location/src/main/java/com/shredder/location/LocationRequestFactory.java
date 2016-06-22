package com.shredder.location;

import com.google.android.gms.location.LocationRequest;

public class LocationRequestFactory {
    public LocationRequest createRequest(LocationConfig config) {
        switch (config.getAccuracy()) {
            case Lowest:
                return createRequest(60000, LocationRequest.PRIORITY_NO_POWER);
            case Low:
                return createRequest(10000, LocationRequest.PRIORITY_LOW_POWER);
            case High:
                return createRequest(5000, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            case Highest:
            default:
                return createRequest(5000, LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }

    private LocationRequest createRequest(int interval, int priority) {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(priority);
        return mLocationRequest;
    }
}
