package com.andreeanita.lvlup.gpsTracking;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;


import com.andreeanita.lvlup.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.List;

public class GPSActivity extends AppCompatActivity {


    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 200;
    private Polyline gpsTrack;
    private LatLng lastKnownLatLng;
    private static MapFragment mapFragment;
    private LocationCallback locationCallback;
    private int startPoint=0;
    private LatLng prev;
    private LatLng current;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastlocation();

        Button btnStart = (Button) findViewById(R.id.startGPSButton);
        btnStart.setOnClickListener(v -> startLocationUpdates());

        Button btnStop = (Button) findViewById(R.id.stopGPSButton);
        btnStop.setOnClickListener(v -> stopLocationUpdates());
    }


    private void fetchLastlocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                /*Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "," +
                        currentLocation.getLongitude(), Toast.LENGTH_LONG).show();*/


                if (mapFragment != null) {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are here");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            googleMap.addMarker(markerOptions);

                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLastlocation();
            }
        }
    }


    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback, Looper.getMainLooper());

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                /*if (locationResult == null) {
                    return;
                }*/
                for (Location location : locationResult.getLocations()) {
                    if (ActivityCompat.checkSelfPermission(GPSActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(GPSActivity.this,
                                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "don't have permission", Toast.LENGTH_SHORT).show();
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    if (mapFragment != null) {
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                if (startPoint==0){
                                    prev=current;
                                    startPoint=1;
                                }
                                current = new LatLng(location.getLatitude(), location.getLongitude());
                                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(current, 25);
                                googleMap.animateCamera(update);
                                googleMap.addPolyline((new PolylineOptions())
                                        .add(prev, current).width(6).color(Color.BLUE)
                                        .visible(true));
                                prev=current;
                                current = null;
                            }
                        });
                    }

                }

                //fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                                            //locationCallback, Looper.getMainLooper());


                /*if (mapFragment != null) {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            if (startPoint==0){
                                prev=current;
                                startPoint=1;
                            }
                            current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(current, 25);
                            googleMap.animateCamera(update);
                            googleMap.addPolyline((new PolylineOptions())
                                    .add(prev, current).width(6).color(Color.BLUE)
                                    .visible(true));
                            prev=current;
                            current = null;
                        }
                    });
                }*/

            }
        };
    }

    private void stopLocationUpdates() {
        fetchLastlocation();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    public void onLocationChanged(Location location) {
        lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        updateTrack();
    }

    private void updateTrack() {
        List<LatLng> points = gpsTrack.getPoints();
        points.add(lastKnownLatLng);
        gpsTrack.setPoints(points);

    }
}