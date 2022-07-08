package com.aagsdevelopment.busplus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.aagsdevelopment.busplus.Database.DriversSessionManager;
import com.aagsdevelopment.busplus.Database.UserSessionManager;
import com.aagsdevelopment.busplus.Driver.DriverDashboard;
import com.aagsdevelopment.busplus.Driver.driverLogin;
import com.aagsdevelopment.busplus.User.UserDashboard;
import com.aagsdevelopment.busplus.User.UserLogin;

public class LoginAs extends AppCompatActivity {

    ConstraintLayout btnStudent, btnDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_as);

        btnStudent = findViewById(R.id.btnloginAsStudent);
        btnDriver = findViewById(R.id.btnloginAsDriver);

        btnStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserSessionManager sessionManager = new UserSessionManager(LoginAs.this, UserSessionManager.SESSION_USERSSESSION);

                if (sessionManager.checkLogin()) {
                    if (UserSessionManager.KEY_USERNAME != null || UserSessionManager.KEY_PASSWORD != null) {
                        Intent intent = new Intent(LoginAs.this, UserDashboard.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(LoginAs.this, UserLogin.class);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(LoginAs.this, UserLogin.class);
                    startActivity(intent);
                }
            }
        });
        btnDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DriversSessionManager driversSessionManager = new DriversSessionManager(LoginAs.this, DriversSessionManager.SESSION_DRIVERSESSION);

                if (driversSessionManager.checkLogin()) {
                    if (DriversSessionManager.KEY_DRIVERUSERNAME != null || DriversSessionManager.KEY_DRIVERPASSWORD != null) {
                        Intent intent = new Intent(LoginAs.this, DriverDashboard.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(LoginAs.this, driverLogin.class);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(LoginAs.this, driverLogin.class);
                    startActivity(intent);
                }
            }
        });


    }
}