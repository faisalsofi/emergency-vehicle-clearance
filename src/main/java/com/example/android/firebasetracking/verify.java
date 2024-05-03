package com.example.android.firebasetracking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class verify extends AppCompatActivity {
Button verify ;
SharedPreferences pref;
int otp;
TextView t;
EditText e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        pref = getSharedPreferences("user_info", 0);
        t = findViewById(R.id.text);
        e = findViewById(R.id.editText);
        verify = findViewById(R.id.button);
        otp = new Random().nextInt(8999)+1000;
        getSupportActionBar().hide();
        final String pnum = pref.getString("phnum",null);
        t.setText("Enter the code that we send to you number "+pnum);
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try  {
//                    URL url = new URL("https://api.textlocal.in/send/?apikey=u3ihfRHLwhE-C2Ara4KHT7HyikIPvLli1GN9zujBKc&numbers="
//                            +pnum+
//                            "&message=Your OTP for Health is "
//                            +(otp)+
//                            "&sender=TXTLCL");
//                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                    try {
//                           InputStream i3n = new BufferedInputStream(urlConnection.getInputStream());
//                        //   readStream(in);
//                    }
//                    finally {
//                        urlConnection.disconnect();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
        e.setText(String.valueOf(otp));
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(otp == Integer.parseInt(String.valueOf(e.getText())))
                {
                    pref.edit().putBoolean("logged", true).apply();

                    Intent intent = new Intent(verify.this, MapsActivity.class);
                startActivity(intent);
                Context c = user_login.getcontext();
                    ((user_login)c).finish();
                    finish();
                }
                else
                {
                    t.setText("Ops! seems u entered wrong OTP");
                }
            }
        });
    }

}
