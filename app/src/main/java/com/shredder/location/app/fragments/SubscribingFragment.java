package com.shredder.location.app.fragments;

import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shredder.location.LocationAccuracy;
import com.shredder.location.app.R;
import com.shredder.location.app.base.BaseFragment;
import com.shredder.location.app.location.LocationPacket;
import com.shredder.location.app.location.LocationSync;
import com.shredder.location.app.util.LatLngInterpolator;
import com.shredder.location.app.util.MarkerAnimation;
import com.shredder.mqtt.QualityOfService;

import butterknife.ButterKnife;

public class SubscribingFragment extends BaseFragment {

    private static final String BOOKING_TITLE = "BOOKING_TITLE";
    private String title;
    private GoogleMap mMap;
    private LocationSync locationSync;
    private Marker marker;
    private String uniqueId;

    public static SubscribingFragment newInstance(String title) {
        SubscribingFragment newFragment = new SubscribingFragment();
        Bundle args = new Bundle();
        args.putSerializable(BOOKING_TITLE, title);
        newFragment.setArguments(args);
        return newFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        title = (String) args.getSerializable(BOOKING_TITLE);
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Must supply a book title");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscriber, container, false);
        ButterKnife.bind(this, view);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.map, mapFragment, "MAP_FRAGMENT").commit();
        mapFragment.getMapAsync(createMapReadyCallback());
        uniqueId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        return view;
    }

    private void updateMarker(LocationPacket location) {
        if (marker == null) {
            return;
        }
        final LatLng position = new LatLng(location.getLat(), location.getLon());

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MarkerAnimation.animateMarkerToICS(marker, position, new LatLngInterpolator.Spherical());
                mMap.animateCamera(CameraUpdateFactory.newLatLng(position));
            }
        });
    }

    @NonNull
    private OnMapReadyCallback createMapReadyCallback() {
        return new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                // Add a marker in Sydney, Australia, and move the camera.
                LatLng sydney = new LatLng(-34, 151);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(sydney)
                        .title("Driver on his way");
                marker = mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                LocationSync.Configuration config = new LocationSync.Configuration() {
                    @Override
                    public LocationAccuracy getAccuracy() {
                        return LocationAccuracy.Lowest;
                    }

                    @Override
                    public String getHost() {
                        return "tcp://broker.hivemq.com:1883";
                    }

                    @Override
                    public String getTopic() {
                        return title;
                    }

                    @Override
                    public QualityOfService getQualityOfService() {
                        return QualityOfService.FireAndForget;
                    }

                    @Override
                    public String getUniqueId() {
                        return uniqueId;
                    }
                };
                locationSync = new LocationSync(config);
                locationSync.setOnLocationReceivedListener(new LocationSync.Listener() {
                    @Override
                    public void onLocationReceived(LocationPacket location) {
                        updateMarker(location);
                    }
                });
            }
        };
    }

    @Override
    protected String getTitle() {
        return title;
    }


}
