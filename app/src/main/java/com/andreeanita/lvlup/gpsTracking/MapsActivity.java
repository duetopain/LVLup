package com.andreeanita.lvlup.gpsTracking;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.andreeanita.lvlup.R;
import com.andreeanita.lvlup.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class
MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    protected GoogleMap mMap;
    protected ActivityMapsBinding binding;
    protected FusedLocationProviderClient fusedLocationProviderClient;
    protected Location currentLocation;
    protected static final int REQUEST_CODE = 200;
    protected Location prevLocation;
    private static long finishDateTime;
    private static long startDateTime;
    private static long time;
    private static String timeElapsed;
    private static byte[] image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        startDateTime = Calendar.getInstance().getTimeInMillis();
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime startDateTime = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            dateTimeFormatter.format(startDateTime);
        }*/

        Button stop= (Button) findViewById(R.id.stopButton);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setMessage("Do you want to save this activity?");
                builder.setCancelable(true);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finishDateTime=Calendar.getInstance().getTimeInMillis();
                                time =startDateTime-finishDateTime;
                                timeElapsed=calculateTime(time);
                                Toast.makeText(getApplicationContext(), timeElapsed, Toast.LENGTH_SHORT).show();
                            }
                        });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                deleteActivity();
                            }
                        });

                AlertDialog alert11 = builder.create();
                alert11.show();
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        fetchLocation();

    }

    public void fetchLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                prevLocation=currentLocation;
                currentLocation = location;

                LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
               // mMap.addMarker(new MarkerOptions().position(current).title("Marker in Sydney"));
                Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "," +
                        currentLocation.getLongitude(), Toast.LENGTH_LONG).show();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 20));
                mMap.setMyLocationEnabled(true);
                if(prevLocation!=null){
                mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(prevLocation.getLatitude(), prevLocation.getLongitude()),
                                 new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()))
                        .width(6).color(Color.BLUE)
                        .visible(true));
                }

            }
        });

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        fetchLocation();
    }

    public byte[] saveMapPhoto(){
        mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                image = getBitmapAsByteArray(bitmap);
            }
        });
        return image;

    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    private void deleteActivity(){
        openGPSActivity();
    }

    public void saveActivity(){
        byte[] image=saveMapPhoto();
        time =startDateTime-finishDateTime;
        timeElapsed=calculateTime(time);
    }

    public void openGPSActivity() {
        Intent intent = new Intent(this, GPSActivity.class);
        startActivity(intent);
    }

    public String calculateTime(long time){
        int seconds = (int) (time / 1000) % 60 ;
        int minutes = (int) ((time / (1000*60)) % 60);
        int hours   = (int) ((time / (1000*60*60)) % 24);
        StringBuilder finalTime=new StringBuilder();
        finalTime.append(hours +":"+minutes+":"+seconds);
        return finalTime.toString();
    }

    //public String getDistance(Location )

}
