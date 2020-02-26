package com.traceandgigit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.traceandgigit.retrofit.RetrofitClientInstance;

import java.util.List;
import java.util.Locale;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;

import androidx.drawerlayout.widget.DrawerLayout;


import android.content.Intent;

import android.view.MenuItem;


import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private ImageView mButton1,mButton2,gpsButton,animFrame;
    private LocationManager locationManager;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view1);
        navigationView.setNavigationItemSelectedListener(this);

        mButton1 = findViewById(R.id.image1);
        mButton2 = findViewById(R.id.image2);
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Employee_ScheduleDetails.class);
                startActivity(intent);
            }
        });

        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UploadedImages.class);
                startActivity(intent);
            }
        });


        gpsButton = findViewById(R.id.targetLocation);
        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGPSLocation();
            }
        });
        animFrame = findViewById(R.id.animFrame);
        Log.e("SCHEME", RetrofitClientInstance.BASE_SCHEME);
        Log.e("HOST", RetrofitClientInstance.BASE_HOST);

    }



    private void getGPSLocation() {
        animFrame.setVisibility(View.VISIBLE);
        Glide.with(this).load(R.drawable.location).into(animFrame);
        FetchAddressService addressService = new FetchAddressService(this.getLocalClassName(), this);
        Location gpsLocation = addressService.getLocation(LocationManager.NETWORK_PROVIDER);
        double latitude = 0.0;
        double longitude = 0.0;
        if(gpsLocation != null){
            latitude = gpsLocation.getLatitude();
            longitude = gpsLocation.getLongitude();
            ConvertToAddress.getAddress(latitude,longitude,this,new GeocoderHandler());
            Log.d("LOCATION","Lat is "+ latitude+" and long is "+longitude);
        }else{
            showSettingsAlert();
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            animFrame.setVisibility(View.GONE);
            Log.d("Location",locationAddress);
        }
    }


    public void showSettingsAlert() {
        animFrame.setVisibility(View.GONE);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MainActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.profile_edit) {
            Intent ini = new Intent(this, MainActivity.class);
            startActivity(ini);

        }
        return false;
    }
}
