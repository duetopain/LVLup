package com.andreeanita.lvlup.gpsTracking;

import static com.andreeanita.lvlup.Database.DatabaseHelper.DATABASE_NAME;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.andreeanita.lvlup.Database.DatabaseHelper;
import com.andreeanita.lvlup.R;
import com.andreeanita.lvlup.databinding.ActivityMapsBinding;
import com.andreeanita.lvlup.loginAndRegister.Login;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Calendar;

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
    protected static long time;
    protected static String timeElapsed;
    static byte[] image;
    private static Location startLocation;
    private static Location finishLocation;
    double distance;
    String pace;
    private LocationManager locationManager;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //fetchLocation();

        /*locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //Get the best provider  for the criteria

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //criteria.setPowerRequirement(Criteria.POWER_LOW);
        //criteria.setAccuracy(Criteria.ACCURACY_FINE);

        String bestprovider = locationManager.getBestProvider(criteria, false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        locationManager.requestLocationUpdates(bestprovider,1000, 5, this);*/

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();

        startDateTime = Calendar.getInstance().getTimeInMillis();
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime startDateTime = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            dateTimeFormatter.format(startDateTime);
        }*/

        /*Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if (bundle!=null){
            email=bundle.getString("email","Default");
        }*/


        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        Button stop = (Button) findViewById(R.id.stopButton);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setMessage("Do you want to save this activity?");
                builder.setCancelable(true);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finishDateTime = Calendar.getInstance().getTimeInMillis();
                        time = startDateTime - finishDateTime;
                        timeElapsed = calculateTime(time);
                        Toast.makeText(getApplicationContext(), timeElapsed, Toast.LENGTH_SHORT).show();
                        if (startLocation != null && finishLocation != null) {
                            distance = startLocation.distanceTo(finishLocation);
                        } else {
                            distance = 0;
                        }
                        Toast.makeText(getApplicationContext(), "distance: " + distance + "m", Toast.LENGTH_SHORT).show();
                        pace = calculatePace(distance, time);


                        //fetch user id
                        Intent intent = getIntent();
                        Bundle bundle = intent.getExtras();
                        if (bundle != null) {
                            userEmail = bundle.getString("email", "Default");
                        }

                        //userEmail= getIntent().getStringExtra("email");
                        //Toast.makeText(getApplicationContext(), "user id " + userEmail, Toast.LENGTH_SHORT).show();

                        String query = "SELECT ID from user WHERE email=?";
                        Cursor cursor = db.rawQuery(query,new String[] {userEmail});
                        //cursor.moveToFirst();

                        @SuppressLint("Range")
                        int userId = cursor.getInt(cursor.getColumnIndex("userEmail"));

                        image = saveMapPhoto();

                        boolean insert = databaseHelper.Insert(startDateTime, pace, timeElapsed, distance, image, userId);
                        if (insert == true) {
                            Toast.makeText(getApplicationContext(), "Activity saved ", Toast.LENGTH_SHORT).show();
                            startDateTime = 0;
                            pace = null;
                            timeElapsed = null;
                            distance = 0;
                            image = null;
                            userId = 0;

                            openGPSActivity();
                        }else{
                            Toast.makeText(getApplicationContext(), "Error saving activity", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        fetchLocation();

    }

    public void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                if (startLocation == null) {
                    startLocation = currentLocation;
                }
                prevLocation = currentLocation;
                currentLocation = location;
                finishLocation = location;

                LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "," +
                        currentLocation.getLongitude(), Toast.LENGTH_LONG).show();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 20));
                mMap.setMyLocationEnabled(true);
                if (prevLocation != null) {
                    mMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(prevLocation.getLatitude(), prevLocation.getLongitude()),
                                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
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

    public byte[] saveMapPhoto() {
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

    private void deleteActivity() {
        startDateTime=0;
        openGPSActivity();
    }

    public void openGPSActivity() {
        Intent intent = new Intent(this, GPSActivity.class);
        startActivity(intent);
    }

    public String calculateTime(long time) {
        int seconds = (int) (time / 1000) % 60;
        int minutes = (int) ((time / (1000 * 60)) % 60);
        int hours = (int) ((time / (1000 * 60 * 60)) % 24);
        StringBuilder finalTime = new StringBuilder();
        finalTime.append(hours + ":" + minutes + ":" + seconds);
        return finalTime.toString();
    }

    public String calculatePace(double distance, long time) {
        double pace = (time / 1000) / distance;
        double seconds = pace % 60;
        int minutes = Integer.parseInt(String.valueOf(pace / 60));
        StringBuilder finalPace = new StringBuilder();
        finalPace.append(minutes);
        finalPace.append(":");
        finalPace.append(seconds);
        return finalPace.toString();
    }


}
