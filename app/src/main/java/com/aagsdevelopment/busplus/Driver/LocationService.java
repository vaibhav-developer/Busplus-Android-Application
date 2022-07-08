package com.aagsdevelopment.busplus.Driver;


import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.aagsdevelopment.busplus.Database.DriversSessionManager;
import com.aagsdevelopment.busplus.Database.UserSessionManager;
import com.aagsdevelopment.busplus.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LocationService<DriverUsername> extends Service {
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://busplusvaibhav-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference ref = firebaseDatabase.getReference("Abhinav/Drivers");
    String TAG = "LOCATION_UPDATES";
    String DriverUsername;
    public double latitude, longitude;
    float bearing,speed;
    float speedInKm;
    float roundoffSpeed;


    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (locationResult != null && locationResult.getLastLocation() != null) {
                latitude = locationResult.getLastLocation().getLatitude();
                longitude = locationResult.getLastLocation().getLongitude();
                bearing = locationResult.getLastLocation().getBearing();
               speed=locationResult.getLastLocation().getSpeed();
                speedInKm=speed*18/5;
                roundoffSpeed = Math.round(speedInKm*100)/100;
                Log.d("speedinKm", "onLocationResult: "+speedInKm);
                Log.d("speedinKmRound", "onLocationResult: "+roundoffSpeed);

                DriversSessionManager driversSessionManager = new DriversSessionManager(LocationService.this, DriversSessionManager.SESSION_DRIVERSESSION);
                HashMap<String, String> DriverData = driversSessionManager.getUserDetailFromSession();
                DriverUsername = DriverData.get(DriversSessionManager.KEY_DRIVERUSERNAME);
//                Log.d("DRIVERTEST", "onCreate: " + DriverUsername);

                ref.child(DriverUsername).child("latitude").setValue(latitude);
                ref.child(DriverUsername).child("longitude").setValue(longitude);
                ref.child(DriverUsername).child("bearing").setValue(bearing);
                ref.child(DriverUsername).child("speed").setValue(roundoffSpeed);


//                Toast.makeText(LocationService.this, "Latitude :" + latitude + "\n" + "Longitude" + longitude, Toast.LENGTH_SHORT).show();

//                Log.d(TAG, latitude + "," + longitude);
            }
        }
    };

    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startLocationService() {
        String channelId = "location_notification_channel";
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),
                channelId
        );
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Location Services");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Running");
        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null
                    && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        "Location Service",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

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
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        startForeground(Constants.LOCATION_SERVICE_ID, builder.build());
    }

    private void stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Constants.ACTION_START_LOCATION_SERVICE)) {
                    startLocationService();
                } else if (action.equals(Constants.ACTION_STOP_LOCATION_SERVICE)) {
                    stopLocationService();
                }
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }
}
