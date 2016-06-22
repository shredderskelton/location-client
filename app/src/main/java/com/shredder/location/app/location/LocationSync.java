package com.shredder.location.app.location;

import android.location.Location;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.shredder.location.GoogleLocationProvider;
import com.shredder.location.LocationConfig;
import com.shredder.mqtt.MqttManager;
import com.shredder.mqtt.MqttManagerConfig;

import lombok.Setter;

public class LocationSync {
    private static final String TAG = "LocationSync";
    private final MqttManager mqttManager;
    private final String topic;
    @Setter
    private Listener onLocationReceivedListener;
    private GoogleLocationProvider locationProvider;

    private final GoogleLocationProvider.OnLocationChangedListener onLocationChanged = new GoogleLocationProvider.OnLocationChangedListener() {
        @Override
        public void onLocationChanged(Location result) {
            publishNewLocation(result);
        }
    };

    private final MqttManager.Listener mqttListener = new MqttManager.Listener() {
        @Override
        public void onMessageReceived(String message, String topic) {
            messageReceived(message);
        }
    };

    public LocationSync(Configuration config) {
        this.topic = config.getTopic();
        this.mqttManager = new MqttManager(config, mqttListener);
        this.mqttManager.subscribe(topic);
    }

    public LocationSync(Configuration config, GoogleLocationProvider locationProvider) {
        this(config);
        this.locationProvider = locationProvider;
        this.locationProvider.addLocationListener(onLocationChanged);
        this.locationProvider.setConfig(config);
    }

    public void setLocationConfig(LocationConfig config) {
        locationProvider.setConfig(config);
    }

    private void publishNewLocation(Location result) {
        Gson gson = new Gson();
        LocationPacket location = new LocationPacket(result.getLatitude(), result.getLongitude());
        String locationString = gson.toJson(location);
        mqttManager.publish(locationString, topic);
    }

    private void messageReceived(String message) {
        Gson gson = new Gson();
        LocationPacket location;
        try {
            location = gson.fromJson(message, LocationPacket.class);
            onLocationReceived(location);
        } catch (JsonSyntaxException e) {
            Log.i(TAG, "Ignoring badly formatted message: " + message);
        }
    }

    private void onLocationReceived(LocationPacket location) {
        onLocationReceivedListener.onLocationReceived(location);
    }

    public interface Configuration extends LocationConfig, MqttManagerConfig {
        String getTopic();
    }

    public interface Listener {
        void onLocationReceived(LocationPacket location);
    }

}
