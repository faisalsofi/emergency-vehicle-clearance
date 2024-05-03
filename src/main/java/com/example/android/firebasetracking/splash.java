package com.example.android.firebasetracking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class splash extends AppCompatActivity {
    SharedPreferences pref;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pref = getSharedPreferences("user_info", 0);
                    if (pref.getBoolean("logged", false)) {
                    intent = new Intent(splash.this, MapsActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(splash.this, user_login.class);
                    startActivity(intent);
                }
                finish();
            }
        }, 1000);
    }
}
