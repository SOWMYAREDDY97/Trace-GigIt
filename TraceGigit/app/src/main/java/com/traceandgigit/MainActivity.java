package com.traceandgigit;

import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.traceandgigit.services.CustomerNotificationService;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements Serializable ,NavigationView.OnNavigationItemSelectedListener{

    private static final int FETCH_BOOKINS_JOB_ID = 1;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private LocationManager locationManager;
    private Location location;
    private ProgressDialog mProgressDialog;
    private RecyclerView shopsRecyclerView;
    private ImageView search;
    private FrameLayout searchFrameLayout;
    private ArrayDeque<SearchFragment> mFragmentStack = new ArrayDeque<>();
    private SearchFragment mSearchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        search = findViewById(R.id.searchImage);
        searchFrameLayout = findViewById(R.id.searchFrameLayout);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view1);
        navigationView.setNavigationItemSelectedListener(this);
        mProgressDialog = new ProgressDialog(this);
        getGPSLocation();
        shopsRecyclerView = findViewById(R.id.shopsRecyclerView);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushFragment();
            }
        });

        JobScheduler jobScheduler =
                (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(new JobInfo.Builder(FETCH_BOOKINS_JOB_ID,
                new ComponentName(this, CustomerNotificationService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(300)
                .build());
      /*  mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TakePictureAndUpload.class);
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
            }
        });
        animFrame = findViewById(R.id.animFrame);
        Log.e("SCHEME", RetrofitClientInstance.BASE_SCHEME);
        Log.e("HOST", RetrofitClientInstance.BASE_HOST);*/

    /*    Intent notificationIntent = new Intent(MainActivity.this, CustomerNotificationActivity.class);
        Utils.showNotification(MainActivity.this,"Booking","Your Booking is successfully sent to Saloon. " +
                "We will notify when your booking is acceted",notificationIntent);*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (searchFrameLayout != null && searchFrameLayout.getVisibility() == View.VISIBLE) {
            searchFrameLayout.setVisibility(View.GONE);
            removeFragment(mSearchFragment);
        }
    }

    private void pushFragment() {
        if (mSearchFragment == null) {
            mSearchFragment = new SearchFragment();
        }
        if (searchFrameLayout != null) {
            searchFrameLayout.setVisibility(View.VISIBLE);
        }
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(android.R.id.content, mSearchFragment);
            mFragmentStack.push(mSearchFragment);
            transaction.addToBackStack(null);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.commitAllowingStateLoss();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void removeFragment(Fragment fragment) {
        if (searchFrameLayout != null && searchFrameLayout.getVisibility() == View.VISIBLE) {
            searchFrameLayout.setVisibility(View.GONE);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.remove(fragment);
        transaction.commitAllowingStateLoss();
        if (mFragmentStack.size() == 1) {
            mFragmentStack.pop();

        }
    }

    private void getGPSLocation() {
        showProgressDialog("Please Wait, we are fetching your Location...");
        FetchAddressService addressService = new FetchAddressService(this.getLocalClassName(), this);
        Location gpsLocation = addressService.getLocation(LocationManager.PASSIVE_PROVIDER);
        double latitude = 0.0;
        double longitude = 0.0;
        if(gpsLocation != null){
            hideProgressDialog();
            latitude = gpsLocation.getLatitude();
            longitude = gpsLocation.getLongitude();
            ConvertToAddress.getAddress(latitude,longitude,this,new GeocoderHandler());
            Log.d("LOCATION","Lat is "+ latitude+" and long is "+longitude);
            fetchSaloons(latitude,longitude);
        }else{
            hideProgressDialog();
            showSettingsAlert();
        }
    }

    private void fetchSaloons(final double latitude, final double longitude) {
        showProgressDialog("Getting the nearest Saloons...");
        ParseQuery<ParseUser> saloonQuery = ParseUser.getQuery();
        try{
            saloonQuery.whereEqualTo(Constants.USER_TYPE,true);
            saloonQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    hideProgressDialog();
                    findTheNearestSaloons(objects, latitude, longitude);
                    Log.e("USERS",objects.toString());
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void findTheNearestSaloons(List<ParseUser> objects, double latitude, double longitude) {
        List<ParseUser> inLocation = new ArrayList<>();
        for(int i = 0;i < objects.size();i++){
            ParseUser user = objects.get(i);
            if (user.get(Constants.LAT_LONG) !=  null) {
                String savedLatLong = (String) user.get(Constants.LAT_LONG);
                if (savedLatLong !=  null) {
                    String[] splitLatLong= savedLatLong.split(",");
                    float[] checkPoint = new float[2];
                    checkPoint[0] = Float.parseFloat(splitLatLong[0]);
                    checkPoint[1] = Float.parseFloat(splitLatLong[1]);
                    float[] center = new float[2];
                    center[0] = (float) latitude;
                    center[1] = (float) longitude;
                    if(arePointsNear(checkPoint,center, 9)){
                        inLocation.add(user);
                    }
                }
            }
        }
        if (inLocation.size() > 0) {
            addDataToRecyclerView(inLocation);
        }
    }

    public void addDataToRecyclerView(List<ParseUser> objects) {
        if(shopsRecyclerView == null){
            shopsRecyclerView = findViewById(R.id.shopsRecyclerView);
        }
        if (mSearchFragment == null) {
            mSearchFragment = new SearchFragment();
        }
        removeFragment(mSearchFragment);
        shopsRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        AdapterSaloons saloons = new AdapterSaloons(this, objects);
        saloons.setClickListener(mOnItemClickListener);
        shopsRecyclerView.setAdapter(saloons);
    }


    private boolean arePointsNear(float[] checkPoint,float[] centerPoint,float km) {
        int ky = 40000 / 360;
        double kx = Math.cos(Math.PI * centerPoint[0] / 180.0) * ky;
        double dx = Math.abs(centerPoint[1] - checkPoint[1]) * kx;
        float dy = Math.abs(centerPoint[0] - checkPoint[0]) * ky;
        return Math.sqrt(dx * dx + dy * dy) <= km;
    }

    private void showProgressDialog(String message) {
        if(mProgressDialog != null && !mProgressDialog.isShowing()){
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
    }

    private void showProgressDialog() {
        if(mProgressDialog != null && !mProgressDialog.isShowing()){
            mProgressDialog.show();
        }
    }

    private void hideProgressDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
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
            Log.d("Location",locationAddress);
        }
    }


    public void showSettingsAlert() {
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
            Intent ini = new Intent(this, CustomerProfileActivity.class);
            startActivity(ini);
        }else if(id == R.id.home){
            Intent ini = new Intent(this, MainActivity.class);
            startActivity(ini);
        }else if(id == R.id.settings){
            Intent ini = new Intent(this, SettingsScreen.class);
            startActivity(ini);
        }else if(id == R.id.contactus){
            Intent ini = new Intent(this, ContactUsActivity.class);
            startActivity(ini);
        }
        else if(id == R.id.livechat){
            Intent ini = new Intent(this, LiveChatActivity.class);
            startActivity(ini);
        }
        else if(id == R.id.logout){
            ParseUser.logOut();
            Intent ini = new Intent(this,SignInActivity.class);
            startActivity(ini);
            finish();
        }
        return false;
    }


    public final OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(ParseUser user) {
            showDialogOfOptions(user);

        }
    };

    private SaveCallback mSaveCallBack = new SaveCallback() {
        @Override
        public void done(ParseException e) {

        }
    };

    private void showDialogOfOptions(final ParseUser user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = null;
        if (layoutInflater != null) {
            v = layoutInflater.inflate(R.layout.customer_options_dialog, null);
        }
        if (v == null) return;
        ImageView menuOption = v.findViewById(R.id.saloonMenu);
        ImageView calenderOption = v.findViewById(R.id.saloonSchedule);
        ImageView locationOption = v.findViewById(R.id.saloonLocation);
        ImageView searchOption = v.findViewById(R.id.saloonSearch);
        menuOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CustomerMenu.class);
                intent.putExtra("sampleObject", user);
                startActivity(intent);
            }
        });
        calenderOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,amount_pay.class);
                startActivity(intent);
            }
        });
        locationOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latLong = null;
                if (user.get(Constants.LAT_LONG) != null) {
                    latLong = user.get(Constants.LAT_LONG).toString();
                }
                if(latLong != null){
                    openMaps(latLong);
                }else {
                    Toast.makeText(MainActivity.this,"Can't Open Maps",Toast.LENGTH_LONG).show();
                }

            }
        });
        searchOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent notificationIntent = new Intent(MainActivity.this, CustomerNotificationActivity.class);
                if(TextUtils.isEmpty((String)user.get(Constants.BOOKING_AT_10))){
                    user.put(Constants.BOOKING_AT_10,ParseUser.getCurrentUser().getObjectId());
                    Utils.showNotification(MainActivity.this,"Booking","Your Booking is successfully sent to Saloon. " +
                            "We will notify when your booking is acceted",notificationIntent);
                    user.saveInBackground(mSaveCallBack);

                } else if(TextUtils.isEmpty((String)user.get(Constants.BOOKING_AT_11))){
                    Utils.showNotification(MainActivity.this,"Booking","Your Booking is successfully sent to Saloon. " +
                            "We will notify when your booking is acceted",notificationIntent);
                    user.put(Constants.BOOKING_AT_11,ParseUser.getCurrentUser().getObjectId());
                    user.saveInBackground(mSaveCallBack);
                }
                else if(TextUtils.isEmpty((String)user.get(Constants.BOOKING_AT_12))){
                    Utils.showNotification(MainActivity.this,"Booking","Your Booking is successfully sent to Saloon. " +
                            "We will notify when your booking is acceted",notificationIntent);
                    user.put(Constants.BOOKING_AT_12,ParseUser.getCurrentUser().getObjectId());
                    user.saveInBackground(mSaveCallBack);
                }
                else if(TextUtils.isEmpty((String)user.get(Constants.BOOKING_AT_1))){
                    Utils.showNotification(MainActivity.this,"Booking","Your Booking is successfully sent to Saloon. " +
                            "We will notify when your booking is acceted",notificationIntent);
                    user.put(Constants.BOOKING_AT_1,ParseUser.getCurrentUser().getObjectId());
                    user.saveInBackground(mSaveCallBack);
                }else{
                    Toast.makeText(MainActivity.this,"Sorry No More booking slots available",Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setView(v);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openMaps(String latLong) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=  "+latLong);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }


}