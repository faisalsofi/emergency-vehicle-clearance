package com.example.android.firebasetracking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class user_login extends AppCompatActivity {
    public static final String PH_NUM = "phnum";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    private static Context ctx;
    String name, ph_num, email;
    boolean imgSet = false, error = false;
    SupportMapFragment mapFragment;
    SharedPreferences pref;
    Button signup_button, select;
    ImageView imageView;
    TextInputLayout ph_edt, name_edt, email_edt;
    int PICK_IMAGE_REQUEST = 22;
    Bitmap bmp;
    private Uri filePath, upload_image_path;


    public static String encodeTobase64(Bitmap image) {

        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    public static Context getcontext() {
        return  ctx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
       this.getSupportActionBar().hide();
        signup_button = findViewById(R.id.sign);
        ph_edt = findViewById(R.id.phn);
        name_edt = findViewById(R.id.nm);
        email_edt = findViewById(R.id.eml);
        select = findViewById(R.id.sel);
        ctx  = user_login.this;
//      imageView = findViewById(R.id.im);
        pref = getSharedPreferences("user_info", 0);
//        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = String.valueOf(name_edt.getEditText().getText());
                email = String.valueOf(email_edt.getEditText().getText());
                ph_num = String.valueOf(ph_edt.getEditText().getText());
                error = false;
               // Log.i("hhh",name);


                    if (name.isEmpty()) {
                        name_edt.setError("Name cant be blank");
                        error = true;
                    } else
                        pref.edit().putString(NAME, name).apply();


                    pref.edit().putString(EMAIL, email).apply();


                    if (ph_num.isEmpty()) {
                        ph_edt.setError("Phone cant be blank");
                        error = true;
                    } else
                        pref.edit().putString(PH_NUM, "91"+ph_num).apply();
                    if (!imgSet)
                        Toast.makeText(user_login.this, "Select an image", Toast.LENGTH_SHORT).show();
                    if (imgSet && !error) {
                        try {
                            bmp = MediaStore
                                    .Images
                                    .Media
                                    .getBitmap(getContentResolver(), filePath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        pref.edit().putString("img", encodeTobase64(bmp)).apply();
                        Intent intent = new Intent(user_login.this, verify.class);
                        startActivity(intent);
                    }


            }
        });
    }
    private void SelectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                22);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();

                // Setting image on image view using Bitmap
               imgSet = true;

//                imageView.setImageBitmap(bitmap);

        }

    }

}
