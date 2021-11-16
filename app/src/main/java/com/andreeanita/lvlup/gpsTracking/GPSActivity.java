package com.andreeanita.lvlup.gpsTracking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


import com.andreeanita.lvlup.R;
import com.andreeanita.lvlup.home.HomeActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Calendar;
import java.util.Date;

public class GPSActivity extends AppCompatActivity {


    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 200;
    private static MapFragment mapFragment;
    private LocationCallback locationCallback;
    private static LatLng startPoint;
    private LatLng prev;
    private LatLng current;
    private GoogleMap googleMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        ImageButton btnHome = (ImageButton) findViewById(R.id.homeGPSbutton);
        btnHome.setOnClickListener(view -> openHome());

        ImageButton btnMusic=(ImageButton) findViewById(R.id.musicGPSbutton);
        btnMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        Button btnStart = (Button) findViewById(R.id.startGPSButton);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               openMapsActivity();
            }
        });
    }


    private void fetchLastlocation() {
    }

    protected void startLocationUpdates() {
    }

    private void stopLocationUpdates() {
    }

    public void openHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void openMapsActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }


}