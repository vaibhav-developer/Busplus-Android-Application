package com.aagsdevelopment.busplus.User;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.aagsdevelopment.busplus.Database.ThisDriverSessionManager;
import com.aagsdevelopment.busplus.Database.UserSessionManager;
import com.aagsdevelopment.busplus.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserBottomSheet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserBottomSheet extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextInputEditText BusIDTV;
    Button btnFindBus;
    String DriverUsernameC, BusID_C;

    public UserBottomSheet() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserBottomSheet.
     */
    // TODO: Rename and change types and number of parameters
    public static UserBottomSheet newInstance(String param1, String param2) {
        UserBottomSheet fragment = new UserBottomSheet();
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
        // Inflate the layout for this
        View view = inflater.inflate(R.layout.fragment_user_bottom_sheet, container, false);
        btnFindBus = view.findViewById(R.id.FButton);
        BusIDTV = view.findViewById(R.id.FTextInput);

        btnFindBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDriverId();
            }
        });
        return view;
    }

    private void isDriverId() {
        String userEnteredBusId = BusIDTV.getText().toString();

        DatabaseReference reference = FirebaseDatabase.getInstance("https://busplusvaibhav-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Abhinav/Drivers");

        List<String> userIdList = new ArrayList();
        Query checkDriver = reference.orderByChild("busid").equalTo(userEnteredBusId);
        checkDriver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.d("Snapshot", "onDataChange: " + snapshot);
                if (snapshot.exists()) {
                    for (DataSnapshot Getsnapshot : snapshot.getChildren()) {
                        String Driver_username = Getsnapshot.child("username").getValue().toString();
                        String Bus_id = Getsnapshot.child("busid").getValue().toString();
                        Log.d("IMPORTANT", "onDataChange: " + Driver_username + "/" + Bus_id);
                        ThisDriverSessionManager thisDriverSessionManager = new ThisDriverSessionManager(getContext(), ThisDriverSessionManager.SESSION_USERSSESSION);
                        thisDriverSessionManager.createThisDriverSessionManager(Driver_username, Bus_id);

                        Intent intent=new Intent(getContext(),UserDashboard.class);
                        intent.putExtra("Driverusername",Driver_username);
                        intent.putExtra("Driverbusid",Bus_id);
                        startActivity(intent);
                        getActivity().finish();

                    }
                } else {
                    Toast.makeText(getContext(), "Invalid Bus Id !", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}