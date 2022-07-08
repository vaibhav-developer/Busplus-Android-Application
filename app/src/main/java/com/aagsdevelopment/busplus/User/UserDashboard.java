package com.aagsdevelopment.busplus.User;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.aagsdevelopment.busplus.Database.DriversSessionManager;
import com.aagsdevelopment.busplus.Database.ThisDriverSessionManager;
import com.aagsdevelopment.busplus.Driver.DriverDashboard;
import com.aagsdevelopment.busplus.R;
import com.aagsdevelopment.busplus.databinding.ActivityUserDashboardBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UserDashboard extends FragmentActivity implements OnMapReadyCallback {


    public static LocationListener listener;
    private GoogleMap mMap;
    private ActivityUserDashboardBinding binding;
    double mylatitude, mylongitude;
    float bearing;
    String DriverUsernameChoosen;
    Marker userLocationMarker;
    FloatingActionButton FlotingBtnGetLocation;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://busplusvaibhav-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference ref = firebaseDatabase.getReference("Abhinav/Drivers");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FlotingBtnGetLocation = findViewById(R.id.flotingBtnFindBus);
        FlotingBtnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                UserBottomSheet bottomSheetDialogFragment = new UserBottomSheet();
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());


            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        String DriverU = getIntent().getStringExtra("Driverusername");
        ThisDriverSessionManager thisDriverSessionManager = new ThisDriverSessionManager(UserDashboard.this, ThisDriverSessionManager.SESSION_USERSSESSION);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {


                    mMap = googleMap;
                    if (thisDriverSessionManager.isDriverDataPresent()) {
                        if (thisDriverSessionManager.KEY_THIS_DRIVER_USERNAME != null) {
                            HashMap<String, String> data = thisDriverSessionManager.getThisDriverDetailFromSession();
                            DriverUsernameChoosen = data.get(thisDriverSessionManager.KEY_THIS_DRIVER_USERNAME);
                            Log.d("Driver", "onDataChange: " + DriverUsernameChoosen);
                            mylatitude = snapshot.child(DriverUsernameChoosen).child("latitude").getValue(Double.class);
                            mylongitude = snapshot.child(DriverUsernameChoosen).child("longitude").getValue(Double.class);
                            LatLng latLng = new LatLng(snapshot.child(DriverUsernameChoosen).child("latitude").getValue(Double.class), snapshot.child(DriverUsernameChoosen).child("longitude").getValue(Double.class));
                            bearing = snapshot.child(DriverUsernameChoosen).child("bearing").getValue(float.class);
                            setUserlocationMarker(latLng, bearing);
                        } else {
                            Toast.makeText(UserDashboard.this, "Select Bus First ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(UserDashboard.this, "Select Bus First ", Toast.LENGTH_SHORT).show();
                        UserBottomSheet bottomSheetDialogFragment = new UserBottomSheet();
                        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUserlocationMarker(LatLng latlong, float bearing) {
        if (userLocationMarker == null) {
//            We create a New Marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latlong);
            userLocationMarker = mMap.addMarker(markerOptions.title("This is Location"));
            userLocationMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bustop));
            userLocationMarker.setRotation(bearing);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 17));

        } else {
//            Use the Previouly Created Marker
            userLocationMarker.setPosition(latlong);
            userLocationMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bustop));
            userLocationMarker.setRotation(bearing);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 17));
        }
    }
}