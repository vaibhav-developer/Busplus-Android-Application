package com.aagsdevelopment.busplus.User;

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
import android.widget.Toast;
import com.aagsdevelopment.busplus.Database.UserSessionManager;
import com.aagsdevelopment.busplus.Driver.DriverDashboard;
import com.aagsdevelopment.busplus.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UserLogin extends AppCompatActivity {
    Button ULBtnSignIn, ULBtnNewusersignup;
    TextInputLayout ULusername, ULPassword;
    RelativeLayout UserLoginProgressBar;
    CheckBox rememberMe;
    EditText ULusernameEditText,ULpasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_login);

        ULusername = findViewById(R.id.UL_Username);
        ULPassword = findViewById(R.id.UL_Password);
        ULBtnSignIn = findViewById(R.id.UL_BtnSignIn);
        ULBtnNewusersignup = findViewById(R.id.UL_newSignUp);
        UserLoginProgressBar = findViewById(R.id.UserloginProgressBar);
        rememberMe = findViewById(R.id.loginUserRememberMe);

        ULusernameEditText=findViewById(R.id.UL_UsernameEditText);
        ULpasswordEditText=findViewById(R.id.UL_PasswordEditText);


        UserSessionManager sessionManager = new UserSessionManager(UserLogin.this, UserSessionManager.SESSION_REMEMBERME);
        if (sessionManager.checkRememberMeLogin()) {
            HashMap<String,String> rememberMeDetails=sessionManager.getRememberMeDetailFromSession();
            ULusernameEditText.setText(rememberMeDetails.get(UserSessionManager.KEYSESSION_USERNAME));
            ULpasswordEditText.setText(rememberMeDetails.get(UserSessionManager.KEYSESSION_PASSWORD));

        }

        ULBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser(view);
            }
        });
        ULBtnNewusersignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserSignUp.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private Boolean validateUserName() {

        String val = ULusername.getEditText().getText().toString();

        if (val.isEmpty()) {
            ULusername.setError("Field cannot be empty");
            return false;
        } else {
            ULusername.setError(null);
            ULusername.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = ULPassword.getEditText().getText().toString();

        if (val.isEmpty()) {
            ULPassword.setError("Field cannot be empty");
            return false;
        } else {
            ULPassword.setError(null);
            ULPassword.setErrorEnabled(false);
            return true;
        }
    }

    public void loginUser(View view) {
        if (!validateUserName() | !validatePassword()) {
            return;
        } else {
            UserLoginProgressBar.setVisibility(View.VISIBLE);
            isDriver();
        }
    }

    private void isDriver() {
        String userEnteredUsername = ULusername.getEditText().getText().toString().trim();
        String userEnteredPassword = ULPassword.getEditText().getText().toString().trim();


        if (rememberMe.isChecked()) {
            UserSessionManager sessionManager = new UserSessionManager(UserLogin.this, UserSessionManager.SESSION_REMEMBERME);
            sessionManager.createRememberMeSession(userEnteredUsername, userEnteredPassword);
        }
        DatabaseReference reference = FirebaseDatabase.getInstance("https://busplusvaibhav-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Abhinav/Users");

        Query checkDriver = reference.orderByChild("username").equalTo(userEnteredUsername);
        checkDriver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ULusername.setError(null);
                    ULusername.setErrorEnabled(false);

                    String passwordFromDB = snapshot.child(userEnteredUsername).child("password").getValue(String.class);

                    if (passwordFromDB.equals(userEnteredPassword)) {
                        ULPassword.setError(null);
                        ULPassword.setErrorEnabled(false);

                        String usernameFromDB = snapshot.child(userEnteredUsername).child("username").getValue(String.class);
                        String nameFromDB = snapshot.child(userEnteredUsername).child("name").getValue(String.class);
                        String phoneNoFromDB = snapshot.child(userEnteredUsername).child("phoneNo").getValue(String.class);
                        String emailFromDB = snapshot.child(userEnteredUsername).child("email").getValue(String.class);


                        Intent intent = new Intent(getApplicationContext(), UserDashboard.class);

                        intent.putExtra("username", usernameFromDB);
                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("phoneNo", phoneNoFromDB);
                        intent.putExtra("email", emailFromDB);

//                      Creating Session Manager

                        UserSessionManager sessionManager = new UserSessionManager(UserLogin.this, UserSessionManager.SESSION_USERSSESSION);
                        sessionManager.createLoginSession(nameFromDB, usernameFromDB, passwordFromDB, emailFromDB, phoneNoFromDB);

                        startActivity(intent);
                        UserLoginProgressBar.setVisibility(View.GONE);
                        finish();
                    } else {
                        UserLoginProgressBar.setVisibility(View.GONE);
                        ULPassword.setError("Wrong Password");
                        ULPassword.requestFocus();

                    }
                } else {
                    UserLoginProgressBar.setVisibility(View.GONE);
                    ULusername.setError("Username not Found !");
                    ULusername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                UserLoginProgressBar.setVisibility(View.GONE);
            }
        });

    }
}