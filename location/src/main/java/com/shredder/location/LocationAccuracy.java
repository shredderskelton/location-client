package com.shredder.location;

import lombok.Getter;

public enum LocationAccuracy {
    Lowest(0),
    Low(1),
    High(2),
    Highest(3);

    @Getter
    private final int value;

    LocationAccuracy(int value) {
        this.value = value;
    }
}
