package com.shredder.location;

import android.location.Location;

import lombok.Getter;

public class LocationReportingAdjuster {

    private static final float TEN_KM_PER_HOUR = 2.7f;
    @Getter
    private boolean isMovingFast = false;

    public boolean shouldChange(Location location) {
        if (!isMovingFast && isFast(location)) {
            isMovingFast = true;
            return true;
        }
        if (isMovingFast && !isFast(location)) {
            isMovingFast = false;
            return true;
        }
        return false;
    }

    private boolean isFast(Location location) {
        return location.getSpeed() > TEN_KM_PER_HOUR;
    }
}
