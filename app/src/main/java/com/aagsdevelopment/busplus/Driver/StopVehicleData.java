package com.aagsdevelopment.busplus.Driver;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.aagsdevelopment.busplus.Database.DriversSessionManager;
import com.aagsdevelopment.busplus.Database.SessionSaveBusNumber;
import com.aagsdevelopment.busplus.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StopVehicleData#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StopVehicleData extends BottomSheetDialogFragment {

    TextInputLayout etBusNumber, etStopLocation, etBusCurrentReading;
    String busNumber;
    String stopLocation;
    String busCurrentReading;
    String driverBusNumber;
    String driverEnteredEndingPoint;
    int driverEnteredOdometerReading;
    Button btnStopDriving;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String lastEnteredOdometer;
    private int currentSequence;

    public StopVehicleData() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StopVehicleData.
     */
    // TODO: Rename and change types and number of parameters
    public static StopVehicleData newInstance(String param1, String param2) {
        StopVehicleData fragment = new StopVehicleData();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_stop_vehical_data, container, false);
        etBusCurrentReading = view.findViewById(R.id.BusCurrentReadingEnd);
        etStopLocation = view.findViewById(R.id.etStopLocation);
        btnStopDriving = view.findViewById(R.id.btnStopDriving);
        btnStopDriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EndJourney(view);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    private boolean vailidatestartLocation() {
        String val = etStopLocation.getEditText().getText().toString();

        if (val.isEmpty()) {
            etStopLocation.setError("Field cannot be empty");
            return false;
        } else {
            etStopLocation.setError(null);
            etStopLocation.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateCurrentReading() {
        String val = etBusCurrentReading.getEditText().getText().toString();

        if (val.isEmpty()) {
            etBusCurrentReading.setError("Field cannot be empty");
            return false;
        } else {
            etBusCurrentReading.setError(null);
            etBusCurrentReading.setErrorEnabled(false);
            return true;
        }
    }

    public void EndJourney(View view) {
        if (!validateCurrentReading() | !vailidatestartLocation()) {
            return;
        } else {
            checkData();
        }
    }

    private void checkData() {

        Log.d("checkData", "checkData: ");
        SessionSaveBusNumber sessionSaveBusNumber = new SessionSaveBusNumber(getContext(), SessionSaveBusNumber.SESSION_SAVEBUSNUMBER);
        if (sessionSaveBusNumber.checkBusNumberPresent()) {
            HashMap<String, String> DriverData = sessionSaveBusNumber.getUserDetailFromSession();
            driverBusNumber = DriverData.get(sessionSaveBusNumber.KEY_BUSNUMBER);
            lastEnteredOdometer = DriverData.get(sessionSaveBusNumber.KEY_ODOMETER);
            currentSequence= Integer.parseInt(DriverData.get(sessionSaveBusNumber.KEY_SEQUENCE));

        }
        driverEnteredOdometerReading = Integer.parseInt(etBusCurrentReading.getEditText().getText().toString().trim());
        driverEnteredEndingPoint = etStopLocation.getEditText().getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance("https://busplusvaibhav-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Abhinav/Buses");

        Query checkBusNumber = reference.orderByChild("busnumber").equalTo(driverBusNumber);


        checkBusNumber.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    int LastOdometerFromDB = snapshot.child(driverBusNumber).child("odometer").getValue(Integer.class);
                    Log.d("Query", "checkData: " + LastOdometerFromDB);

                    if (driverEnteredOdometerReading > LastOdometerFromDB) {
                        etBusCurrentReading.setError(null);
                        etBusCurrentReading.setErrorEnabled(false);
                        reference.child(driverBusNumber).child("odometer").setValue(driverEnteredOdometerReading);
                        reference.child(driverBusNumber).child("lastsequence").setValue(currentSequence);
                        reference.child(driverBusNumber).child(String.valueOf(currentSequence)).child("odometer").setValue(driverEnteredOdometerReading);
                        reference.child(driverBusNumber).child(String.valueOf(currentSequence)).child("odometerstop").setValue(driverEnteredOdometerReading);
                        reference.child(driverBusNumber).child(String.valueOf(currentSequence)).child("stoppingat").setValue(driverEnteredEndingPoint);

                        sessionSaveBusNumber.clearBusNumber();
                        dismiss();
                    } else {
                        etBusCurrentReading.setError("Do Not Input Reading Smaller Than " + lastEnteredOdometer + " !");
                        etBusCurrentReading.requestFocus();
                    }

                } else {
                    Toast.makeText(getContext(), "Bus Number Not Found !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });

    }
}