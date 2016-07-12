package com.shredder.location;

import com.google.android.gms.location.LocationRequest;

public class LocationRequestFactory {

    public LocationRequest createRequest(LocationConfig config, boolean isMovingFast) {
        switch (config.getAccuracy()) {
            case Lowest:
                return createRequest(LocationRequest.PRIORITY_NO_POWER, isMovingFast);
            case Low:
                return createRequest(LocationRequest.PRIORITY_LOW_POWER, isMovingFast);
            case High:
                return createRequest(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, isMovingFast);
            case Highest:
            default:
                return createRequest(LocationRequest.PRIORITY_HIGH_ACCURACY, isMovingFast);
        }
    }

    private LocationRequest createRequest(int priority, boolean isMovingFast) {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(isMovingFast ? 2000 : 6000);
        mLocationRequest.setFastestInterval(isMovingFast ? 1000 : 3000);
        mLocationRequest.setPriority(priority);
        mLocationRequest.setSmallestDisplacement(1);
        return mLocationRequest;
    }
}
