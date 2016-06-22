package com.shredder.location.app.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.shredder.location.GoogleLocationProvider;
import com.shredder.location.LocationAccuracy;
import com.shredder.location.app.R;
import com.shredder.location.app.base.BaseFragment;
import com.shredder.location.app.location.LocationSync;
import com.shredder.mqtt.QualityOfService;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublisherFragment extends BaseFragment {

    public static final String TAG = "MAINTAG";
    private static final int PERMISSIONS_REQUEST_CODE = 4324;
    @Bind(R.id.edit_message_text) EditText editTextMessage;
    @Bind(R.id.button_send) Button buttonControl;
    private LocationSync publisher;
    private GoogleLocationProvider googleLocationProvider;
    private String uniqueId;

    LocationSync.Configuration createConfig() {
        return new LocationSync.Configuration() {
            @Override
            public String getHost() {
                return "tcp://broker.hivemq.com:1883";
            }

            @Override
            public String getTopic() {
                return editTextMessage.getText().toString();
            }

            @Override
            public QualityOfService getQualityOfService() {
                return QualityOfService.FireAndForget;
            }

            @Override
            public String getUniqueId() {
                return uniqueId;
            }

            @Override
            public LocationAccuracy getAccuracy() {
                return LocationAccuracy.Highest;
            }
        };
    }

    public static PublisherFragment newInstance() {
        return new PublisherFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        uniqueId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        editTextMessage.setText("mydrivertest");
        googleLocationProvider = new GoogleLocationProvider(getActivity());
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startStopLocationPublisher();
    }

    @OnClick(R.id.button_send)
    public void onClick() {
        startStopLocationPublisher();
    }

    private void startStopLocationPublisher() {
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
            return;
        }

        if (publisher != null) {
            publisher = null;
            buttonControl.setText("Start");
        } else {
            publisher = new LocationSync(createConfig(), googleLocationProvider);
            buttonControl.setText("Stop");
        }
    }

    private boolean hasPermission() {
        return !(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }

    @OnClick(R.id.button_subscribe)
    public void onSub() {
        add(SubscribingFragment.newInstance(editTextMessage.getText().toString()));
    }

    @Override
    protected String getTitle() {
        return "Write message";
    }
}
