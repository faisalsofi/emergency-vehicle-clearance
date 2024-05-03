package com.example.android.firebasetracking;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("key");
    Button btn1, btn2;
    SupportMapFragment mapFragment;
    String TAG = "hhh";
    EditText e,e_lat,e_lon;
    Marker marker1;
    LatLng currentLocation = new LatLng(34.0, 74.7);
    GoogleMap mMap;
    boolean track;
    ImageView lg;
    SharedPreferences pref;
    Circle circle;
    boolean cir;
    float[] distance = new float[2];
    public static final String ph_num = "phnum";
    public static final String Name = "name";
    public static final String Email = "email";
    private static final int REQUEST_CODE = 101;
    LocationManager locationManager;
    String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        pref = getSharedPreferences("user_info", 0);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btn1 = findViewById(R.id.button);
        btn2 = findViewById(R.id.button2);
        e = findViewById(R.id.editText5);
        e_lat = findViewById(R.id.editText2);
        e_lon = findViewById(R.id.editText3);
        track = false;
        lg = findViewById(R.id.sett2);

        lg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Really Exit?")
                        .setMessage("Are you sure you want to Log Out?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                pref.edit().putBoolean("logged", false).apply();
                                Intent i = new Intent(MapsActivity.this, user_login.class);
                                startActivity(i);
                                locationManager.removeUpdates(MapsActivity.this);
                                finish();
                            }
                        }).create().show();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cir) {
                    circle.remove();
                    cir = false;
                    btn2.setText("Add Traffic Signal");
                } else {
                    btn2.setText("Traffic Signal added");
                    cir = true;
                    LatLng circle_loca =   new LatLng(
                            Double.parseDouble(e_lat.getText().toString()),
                            Double.parseDouble(e_lon.getText().toString()));
                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(circle_loca);
                    circleOptions.radius(100);
                    circleOptions.strokeColor(Color.argb(255, 255, 0, 0));
                    circleOptions.fillColor(Color.argb(64, 255, 0, 0));
                    circleOptions.strokeWidth(4);
                    circle = mMap.addCircle(circleOptions);
                }


            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                track = !track;
                if (track) {
                    btn1.setText("Emergency mode on");
                    marker1.setTitle("Your Location");
                    MovePoniter(currentLocation, marker1);
                    MoveCam(currentLocation);

                }else {
                    btn1.setText("Emergency mode off");
                    myRef.setValue(111);
                }

            }
        });
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(
                MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(
                MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);

        Toast.makeText(this, "resume ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        marker1 = mMap.addMarker(new MarkerOptions().position(currentLocation).title("ll"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }
    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "onMapClick("+latLng +")");
    }
    // Callback called when Marker is touched
    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarke rClickListener: " + marker.getPosition() );
        return false;
    }

    void MovePoniter(final LatLng l, final Marker m) {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap Map) {
                marker1.setPosition(l);
            }
        });
    }
    void MoveCam(final LatLng l) {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap Map) {
                Map.animateCamera(CameraUpdateFactory.newLatLngZoom(l, 17));
            }
        });
    }


    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        System.exit(1);
                        track = false;
                        locationManager.removeUpdates(MapsActivity.this);
                    }
                }).create().show();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = new LatLng(location.getLatitude(),location.getLongitude());
        MovePoniter(currentLocation, marker1);
        MoveCam(currentLocation);

        if(cir  && track){
//            Toast.makeText(this, String.valueOf(location.getLatitude())+" in "+String.valueOf(location.getLongitude()), Toast.LENGTH_SHORT).show();
            Location.distanceBetween(currentLocation.latitude, currentLocation.longitude, circle.getCenter().latitude,circle.getCenter().longitude,distance);

            if ( distance[0] <= circle.getRadius())
            {
                Log.i("cir", String.valueOf(cir));
                Log.i("track1", String.valueOf(track));
                Toast.makeText(this, String.valueOf(location.getLatitude())+" in "+String.valueOf(location.getLongitude()), Toast.LENGTH_SHORT).show();
             String s = e.getText().toString();
             if(s.isEmpty())
                 myRef.setValue(111);
             else
                myRef.setValue(Integer.parseInt(s));
            }
            else
            {
                Log.i("cirjj", String.valueOf(cir));
                Log.i("track1", String.valueOf(track));
                Toast.makeText(this, String.valueOf(location.getLatitude())+" out "+String.valueOf(location.getLongitude()), Toast.LENGTH_SHORT).show();
                myRef.setValue(111);
            }
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}