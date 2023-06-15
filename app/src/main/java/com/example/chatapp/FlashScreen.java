package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class FlashScreen extends AppCompatActivity {
    private  static int Splash_timer =3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_screen);
//       // splash screen does not require title bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // after 3sec navigating to main screen
                Intent intent = new Intent(FlashScreen.this, MainActivity.class);
                startActivity(intent);
//                finish is used when user press back it should not redirect to splash
                finish();
            }
        },Splash_timer);
    }
}