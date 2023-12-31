package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class otpAuthenication extends AppCompatActivity {
TextView mchangenumber;

EditText mgetotp;
android.widget.Button mverifyotp;
String enteredotp;

FirebaseAuth firebaseAuth;
ProgressBar mprogressbarofotpauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_authenication);
        mchangenumber = findViewById(R.id.changeNumber);
        mgetotp = findViewById(R.id.getotp);
        mverifyotp = findViewById(R.id.verifyotp);
        mprogressbarofotpauth = findViewById(R.id.progressbarofotpAuth);
        firebaseAuth = FirebaseAuth.getInstance();
        mchangenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(otpAuthenication.this,MainActivity.class);
                startActivity(intent);
            }
        });
        mverifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredotp = mgetotp.getText().toString();
                if(enteredotp.isEmpty()){
                    Toast.makeText(getApplicationContext(), "enter the otp first", Toast.LENGTH_SHORT).show();
                }
                else{
                    mprogressbarofotpauth.setVisibility(View.VISIBLE);
                    String coderecived = getIntent().getStringExtra("otp");
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(coderecived,enteredotp);
                    signInWithPhoneAuthCredential(credential);

                }
            }
        });
    }
    private  void signInWithPhoneAuthCredential(PhoneAuthCredential credential){
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mprogressbarofotpauth.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(otpAuthenication.this,setProfile.class);
                    Toast.makeText(getApplicationContext(),"Logged in successful",Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
                else{
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                    {
                        mprogressbarofotpauth.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),"Login failed",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}