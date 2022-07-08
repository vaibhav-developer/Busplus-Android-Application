package com.aagsdevelopment.busplus.Driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.aagsdevelopment.busplus.Database.DriversSessionManager;
import com.aagsdevelopment.busplus.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class driverLogin extends AppCompatActivity {
    Button DLBtnSignIn;
    TextInputLayout DLusername, DlPassword;
    CheckBox driverRememberMe;
    RelativeLayout loginProgressBar;
    EditText DLusernameEditText, DlpasswordEditText;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://busplusvaibhav-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference ref=firebaseDatabase.getReference("Abhinav/Drivers");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_driver_login);

        DLusername = findViewById(R.id.Busid);
        DlPassword = findViewById(R.id.DLPassword);
        DLBtnSignIn = findViewById(R.id.btnFindBus);
        loginProgressBar = findViewById(R.id.loginProgressBar);
        driverRememberMe = findViewById(R.id.driverRememberMe);


        DriversSessionManager sessionManager = new DriversSessionManager(driverLogin.this, DriversSessionManager.SESSION_DRIVERREMEMBERME);
        if (sessionManager.checkRememberMeLogin()) {
            HashMap<String, String> rememberMeDetails = sessionManager.getRememberMeDetailFromSession();
            DLusernameEditText.setText(rememberMeDetails.get(DriversSessionManager.KEYDRIVERSESSION_USERNAME));
            DlpasswordEditText.setText(rememberMeDetails.get(DriversSessionManager.KEYDRIVERSESSION_PASSWORD));

        }

        DLBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginDriver(view);
            }
        });
    }

    private Boolean validateUserName() {
        String val = DLusername.getEditText().getText().toString();

        if (val.isEmpty()) {
            DLusername.setError("Field cannot be empty");
            return false;
        } else {
            DLusername.setError(null);
            DLusername.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = DlPassword.getEditText().getText().toString();

        if (val.isEmpty()) {
            DlPassword.setError("Field cannot be empty");
            return false;
        } else {
            DlPassword.setError(null);
            DlPassword.setErrorEnabled(false);
            return true;
        }
    }

    public void loginDriver(View view) {
        if (!validateUserName() | !validatePassword()) {
            return;
        } else {
            loginProgressBar.setVisibility(View.VISIBLE);
            isDriver();
        }
    }

    private void isDriver() {
        String driverEnteredUsername = DLusername.getEditText().getText().toString().trim();
        String driverEnteredPassword = DlPassword.getEditText().getText().toString().trim();

//        Shared Preferences
        if (driverRememberMe.isChecked()) {
            DriversSessionManager sessionManager = new DriversSessionManager(driverLogin.this, DriversSessionManager.SESSION_DRIVERREMEMBERME);

            sessionManager.createDriverRememberMeSession(driverEnteredUsername, driverEnteredPassword);
        }


        Query checkDriver = ref.orderByChild("username").equalTo(driverEnteredUsername);
        checkDriver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DLusername.setError(null);
                    DLusername.setErrorEnabled(false);

                    String passwordFromDB = snapshot.child(driverEnteredUsername).child("password").getValue(String.class);

                    if (passwordFromDB.equals(driverEnteredPassword)) {
                        DlPassword.setError(null);
                        DlPassword.setErrorEnabled(false);

                        String usernameFromDB = snapshot.child(driverEnteredUsername).child("username").getValue(String.class);

//                        Creating Session Manager

                        DriversSessionManager DsessionManager = new DriversSessionManager(driverLogin.this, DriversSessionManager.SESSION_DRIVERSESSION);
                        DsessionManager.createDriverLoginSession(usernameFromDB,passwordFromDB);


                        Intent intent = new Intent(getApplicationContext(), DriverDashboard.class);
                        startActivity(intent);
                        loginProgressBar.setVisibility(View.GONE);
                        finish();
                    } else {
                        loginProgressBar.setVisibility(View.GONE);
                        DlPassword.setError("Wrong Password");
                        DlPassword.requestFocus();

                    }
                } else {
                    loginProgressBar.setVisibility(View.GONE);
                    DLusername.setError("Username not Found !");
                    DLusername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loginProgressBar.setVisibility(View.GONE);

            }
        });

    }
}