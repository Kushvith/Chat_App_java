package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
EditText mgetphonenumber;
android.widget.Button msendotp;
CountryCodePicker mcountryCodePicker;
String countrycode;
String phonenumber;

FirebaseAuth firebaseAuth;
ProgressBar mprogressBarOfMain;

PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
String codesent;

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            Intent intent = new Intent(MainActivity.this,chatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mcountryCodePicker = findViewById(R.id.countrycodepicker);
        mgetphonenumber = findViewById(R.id.phonenumber);
        msendotp =findViewById(R.id.otpbtn);
        mprogressBarOfMain = findViewById(R.id.progressbarofmain);

//        getting the instance of user to check logged in or not
        firebaseAuth = FirebaseAuth.getInstance();
//        firebase required number with +.it picks default number
        countrycode = mcountryCodePicker.getSelectedCountryCodeWithPlus();
//      if user changes the country code than the default country
        mcountryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countrycode = mcountryCodePicker.getSelectedCountryCodeWithPlus();
            }
        });

        msendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number;
                number = mgetphonenumber.getText().toString();
                if(number.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Enter the Number",Toast.LENGTH_SHORT).show();
                } else if (number.length() < 10) {
                    Toast.makeText(getApplicationContext(),"Please Enter Correct Number",Toast.LENGTH_SHORT).show();
                }
                else{
                    mprogressBarOfMain.setVisibility(view.VISIBLE);
                    phonenumber = countrycode+number;
                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(phonenumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(MainActivity.this)
                            .setCallbacks(mCallBacks)
                            .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            }
        });
        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                // how to automatically fetch code here
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(getApplicationContext(),"Otp sent",Toast.LENGTH_SHORT).show();
                mprogressBarOfMain.setVisibility(View.INVISIBLE);
                codesent=s;
                Intent intent = new Intent(MainActivity.this, otpAuthenication.class);
                intent.putExtra("otp",codesent);
                startActivity(intent);
            }
        };

    }
}