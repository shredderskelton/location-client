package com.shredder.location.app.location;

import lombok.Data;

@Data
public class LocationPacket {
    private String userField;
    private final double lat;
    private final double lon;
}
