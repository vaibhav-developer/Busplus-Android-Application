package com.aagsdevelopment.busplus.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.aagsdevelopment.busplus.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserSignUp extends AppCompatActivity {

    TextInputLayout usuFullName,usuUserName,usuEmail,usuPhoneNumber,usuPassword,usuConfirmPassword;
    Button usuBtnSignup,usuBtnAlreadyHaveAnAccount;

//    DataBase

    FirebaseDatabase rootNode=FirebaseDatabase.getInstance("https://busplusvaibhav-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);


//        Hooks
        usuFullName=findViewById(R.id.USU_FullName);
        usuUserName=findViewById(R.id.USU_Username);
        usuEmail=findViewById(R.id.USU_Email);
        usuPhoneNumber=findViewById(R.id.USU_PhoneNumber);
        usuPassword=findViewById(R.id.USU_Password);
        usuConfirmPassword=findViewById(R.id.USU_ConfirmPassword);
        usuBtnSignup=findViewById(R.id.USU_btnSignup);
        usuBtnAlreadyHaveAnAccount=findViewById(R.id.USU_btnAlreadyHaveAnAccount);

         usuBtnAlreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(getApplicationContext(),UserLogin.class);
                 startActivity(intent);
                 finish();
             }
         });
//        Saving Data in DataBase on btn Click

        usuBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference=rootNode.getReference("Abhinav/Users");



//                Get all the Values
                registerUser(view);

            }
        });// SignUpBtn Method End
    } // on Create Methos End


    private Boolean validateName(){
        String val=usuFullName.getEditText().getText().toString();

        if(val.isEmpty()){
            usuFullName.setError("Field cannot be empty");
            return false;
        }
        else{
            usuFullName.setError(null);
            usuFullName.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validateUserName() {
        String val = usuUserName.getEditText().getText().toString();
        String noWhiteSpace = "^\\s*\\S+\\s*$";
        if (val.isEmpty()) {
            usuUserName.setError("Field cannot be empty");
            return false;
        } else if (val.length() >= 15) {
            usuUserName.setError("Username too long");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            usuUserName.setError("White Spaces are Not Allowed");
            return false;
        } else {
            usuUserName.setError(null);
            usuUserName.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validateEmail(){
        String val=usuEmail.getEditText().getText().toString();
        String emailpattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(val.isEmpty()){
            usuEmail.setError("Field cannot be empty");
            return false;
        }else if(!val.matches(emailpattern)){
            usuEmail.setError("Invalid email address");
            return false;
        }
        else{
            usuEmail.setError(null);
            usuEmail.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validatePhoneNo(){
        String val=usuPhoneNumber.getEditText().getText().toString();

        if(val.isEmpty()){
            usuPhoneNumber.setError("Field cannot be empty");
            return false;
        }else{
            usuPhoneNumber.setError(null);
            usuPhoneNumber.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validatePassword(){
        String val=usuPassword.getEditText().getText().toString();
        String passval="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).{8,15}$";

        if(val.isEmpty()){
            usuPassword.setError("Field cannot be empty");
            return false;
        }else if (!val.matches(passval)) {
            usuPassword.setError("Password is too weak");
            return false;
        }
        else{
            usuPassword.setError(null);
            usuPassword.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validateConfirmPassword(){
        String val=usuPassword.getEditText().getText().toString();
        String confPass=usuConfirmPassword.getEditText().getText().toString();

        if(confPass.isEmpty()){
            usuConfirmPassword.setError("Field cannot be empty");
            return false;
        }else if (!val.matches(confPass)) {
            usuConfirmPassword.setError("Password does not match !");
            return false;
        }
        else{
            usuConfirmPassword.setError(null);
            usuConfirmPassword.setErrorEnabled(false);
            return true;
        }
    }
    public void registerUser(View view) {

        if(!validateName() | !validateUserName() |  !validateEmail() | !validatePhoneNo() | !validatePassword() | !validateConfirmPassword()){
            return;
        }

        String name=usuFullName.getEditText().getText().toString();
        String username=usuUserName.getEditText().getText().toString();
        String email=usuEmail.getEditText().getText().toString();
        String phoneNo=usuPhoneNumber.getEditText().getText().toString();
        String password=usuPassword.getEditText().getText().toString();

//        Adding Values to Database
        UserHelperClass helperClass=new UserHelperClass(name,username,email,phoneNo,password);
        reference.child(username).setValue(helperClass);

        Intent intent = new Intent(UserSignUp.this,UserLogin.class);
        startActivity(intent);
        finish();
    }
}