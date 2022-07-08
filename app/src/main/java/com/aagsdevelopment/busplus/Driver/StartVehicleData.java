package com.aagsdevelopment.busplus.Driver;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.aagsdevelopment.busplus.Database.SessionSaveBusNumber;
import com.aagsdevelopment.busplus.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StartVehicleData#newInstance} factory method to
 * create an instance of this fragment.
 */

public class StartVehicleData extends BottomSheetDialogFragment {
    //This class Variables
    TextInputLayout etBusNumber, etStartLocation, etBusCurrentReading;
    String busNumber;
    String startLocation;
    String busCurrentReading;
    String driverEnteredBusNumber;
    String driverEnteredStartingPoint;
    int driverEnteredOdometerReading;
    Button btnStartDriving;

    int sequence;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public StartVehicleData() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StartVehicleData.
     */
    // TODO: Rename and change types and number of parameters
    public static StartVehicleData newInstance(String param1, String param2) {
        StartVehicleData fragment = new StartVehicleData();
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

        View view = inflater.inflate(R.layout.fragment_start_vehicale_data, container, false);
        etBusNumber = view.findViewById(R.id.etBusNumber);
        etBusCurrentReading = view.findViewById(R.id.BusCurrentReading);
        etStartLocation = view.findViewById(R.id.etStartLocation);
        btnStartDriving = view.findViewById(R.id.btnStartDriving);

        startLocation = etStartLocation.getEditText().getText().toString();
        busNumber = etBusNumber.getEditText().getText().toString();
        busCurrentReading = etBusCurrentReading.getEditText().getText().toString();
        Log.d("TAG", "onCreateView: ");
        btnStartDriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProceedforDriving(view);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    private boolean vailidateBusNumber() {
        String val = etBusNumber.getEditText().getText().toString();

        if (val.isEmpty()) {
            etBusNumber.setError("Field cannot be empty");
            return false;
        } else {
            etBusNumber.setError(null);
            etBusNumber.setErrorEnabled(false);
            return true;
        }
    }

    private boolean vailidatestartLocation() {
        String val = etStartLocation.getEditText().getText().toString();

        if (val.isEmpty()) {
            etStartLocation.setError("Field cannot be empty");
            return false;
        } else {
            etStartLocation.setError(null);
            etStartLocation.setErrorEnabled(false);
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

    public void ProceedforDriving(View view) {
        if (!validateCurrentReading() | !vailidateBusNumber() | !vailidatestartLocation()) {
            return;
        } else {
            checkData();
        }
    }

    private void checkData() {

        Log.d("checkData", "checkData: ");
        driverEnteredBusNumber = etBusNumber.getEditText().getText().toString().trim();
        driverEnteredOdometerReading = Integer.parseInt(etBusCurrentReading.getEditText().getText().toString().trim()) ;
        driverEnteredStartingPoint = etStartLocation.getEditText().getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance("https://busplusvaibhav-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Abhinav/Buses");

        Query checkBusNumber = reference.orderByChild("busnumber").equalTo(driverEnteredBusNumber);


        checkBusNumber.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    int  LastOdometerFromDB=  snapshot.child(driverEnteredBusNumber).child("odometer").getValue(Integer.class);

                    Log.d("Query", "checkData: " + LastOdometerFromDB);

                    if(driverEnteredOdometerReading>=LastOdometerFromDB ){
                        sequence=snapshot.child(driverEnteredBusNumber).child("lastsequence").getValue(Integer.class);
                        int currentSequence=sequence+1;
                        etBusCurrentReading.setError(null);
                        etBusCurrentReading.setErrorEnabled(false);

                        Log.d("sequence", "onDataChange: "+sequence);
                        reference.child(driverEnteredBusNumber).child(String.valueOf(currentSequence)).child("odometer").setValue(driverEnteredOdometerReading);
                        reference.child(driverEnteredBusNumber).child("odometer").setValue(driverEnteredOdometerReading);
                        reference.child(driverEnteredBusNumber).child(String.valueOf(currentSequence)).child("odometerstart").setValue(driverEnteredOdometerReading);
                        reference.child(driverEnteredBusNumber).child(String.valueOf(currentSequence)).child("startingfrom").setValue(driverEnteredStartingPoint);
                        SessionSaveBusNumber saveBusNumber=new SessionSaveBusNumber(getContext(),SessionSaveBusNumber.SESSION_SAVEBUSNUMBER);
                        saveBusNumber.createSaveBusNumberSession(driverEnteredBusNumber,Integer.toString(driverEnteredOdometerReading),Integer.toString(currentSequence));
                        dismiss();
                    }else{
                        etBusCurrentReading.setError("Do Not Input Reading Smaller Than Last Trip !");
                        etBusCurrentReading.requestFocus();

                    }

                }else{
                    Toast.makeText(getContext(), "Bus Number Not Found !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}