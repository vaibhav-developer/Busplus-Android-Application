package com.aagsdevelopment.busplus.Driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aagsdevelopment.busplus.Database.DriversSessionManager;
import com.aagsdevelopment.busplus.MainActivity;
import com.aagsdevelopment.busplus.R;
import com.aagsdevelopment.busplus.User.UserBottomSheet;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class DriverDashboard extends AppCompatActivity implements OnMapReadyCallback {

    public String DriverUserName;
    private GoogleMap mMap;

    //
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://busplusvaibhav-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference ref = firebaseDatabase.getReference("Abhinav/Drivers");
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    //    Button btnStartLocationUpdate, btnStopLocationUpdate;
    LottieAnimationView toggleOnOff;
    private boolean switchOn = false;
    double mylatitude;
    double mylongitude;
    TextView TvSpeed;
    float bearing;
    Object speedInKm;
    Marker userLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);
        toggleOnOff = findViewById(R.id.lottieOnOff);
        TvSpeed = findViewById(R.id.tvSpeed);
        DriversSessionManager driversSessionManager = new DriversSessionManager(DriverDashboard.this, DriversSessionManager.SESSION_DRIVERSESSION);
        HashMap<String, String> DriverData = driversSessionManager.getUserDetailFromSession();
        DriverUserName = DriverData.get(DriversSessionManager.KEY_DRIVERUSERNAME);
        Log.d("DRIVERTEST", "onCreate: " + DriverUserName);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.Driver_Map);
        mapFragment.getMapAsync(this);

        if (switchOn) {
            toggleOnOff.setMinAndMaxProgress(0.0f, 0.5f);
            toggleOnOff.playAnimation();
            toggleOnOff.setMinProgress(0.5f);
        }
        toggleOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchOn) {
                    toggleOnOff.setMinAndMaxProgress(0.5f, 1.0f);
                    toggleOnOff.playAnimation();
                    switchOn = false;
                    if (isLocationServiceRunning()) {
                        StopVehicleData bottomSheetDialogFragment = new StopVehicleData();
                        bottomSheetDialogFragment.setCancelable(false);
                        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

                    }
                    
                    stopLocationService();
                } else {
                    toggleOnOff.setMinAndMaxProgress(0.0f, 0.5f);
                    toggleOnOff.playAnimation();
                    switchOn = true;
                    if (!isLocationServiceRunning()) {
                        StartVehicleData bottomSheetDialogFragment = new StartVehicleData();
                        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                        bottomSheetDialogFragment.setCancelable(false);
                    }
                    if (ContextCompat.checkSelfPermission(
                            getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                                DriverDashboard.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
                    } else {
                        startLocationService();
                    }

                }
            }
        });
//        btnStartLocationUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ContextCompat.checkSelfPermission(
//                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(
//                            DriverDashboard.this,
//                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
//                } else {
//                    startLocationService();
//                }
//            }
//        });
//
//        btnStopLocationUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                stopLocationService();
//            }
//        });
    }

    @Override
    public void onBackPressed() {

        //  super.onBackPressed();
        if (isLocationServiceRunning()) {
            Dialog dialog = new Dialog(DriverDashboard.this);
            dialog.setContentView(R.layout.driverdialogue);
            dialog.show();

            dialog.findViewById(R.id.txtClose).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        } else {
            DriverDashboard.super.onBackPressed();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service :
                    activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location Service Started ", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location Service Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mylatitude = snapshot.child(DriverUserName).child("latitude").getValue(Double.class);
                    mylongitude = snapshot.child(DriverUserName).child("longitude").getValue(Double.class);
                    bearing = snapshot.child(DriverUserName).child("bearing").getValue(float.class);
                    speedInKm = snapshot.child(DriverUserName).child("speed").getValue(Double.class);
                    Log.d("Speed", "onDataChange: " + speedInKm);
                    if (isLocationServiceRunning()) {
                        TvSpeed.setText(speedInKm.toString());
                    }
//                Toast.makeText(DriverDashboard.this, mylatitude + "/" + mylongitude + "/" + bearing+"/"+DriverUserName, Toast.LENGTH_SHORT).show();
                    LatLng latLng = new LatLng(snapshot.child(DriverUserName).child("latitude").getValue(Double.class), snapshot.child(DriverUserName).child("longitude").getValue(Double.class));
//                userLocationMarker.setRotation((float) bearing);
                    setUserlocationMarker(latLng, bearing);
                } else {
                    Toast.makeText(DriverDashboard.this, "location not found !", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.toException();
            }
        });
    }

    private void setUserlocationMarker(LatLng latlong, float bearing) {
        if (userLocationMarker == null) {
//            We create a New Marker
//         TvSpeed.setText((CharSequence) speedInKm);
            LatLng newLatlong = new LatLng(latlong.latitude, latlong.longitude);
            userLocationMarker = mMap.addMarker(new MarkerOptions().position(newLatlong).title("Bus is Here !"));
            userLocationMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bustop));
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


            userLocationMarker.setRotation(bearing);
            userLocationMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bustop));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLatlong, 18));
        } else {
//            Use the Previouly Created Marker
            LatLng newLatlong = new LatLng(latlong.latitude, latlong.longitude);
            userLocationMarker.setPosition(newLatlong);
            userLocationMarker.setRotation(bearing);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 18));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLatlong, 18));


        }
    }

}


