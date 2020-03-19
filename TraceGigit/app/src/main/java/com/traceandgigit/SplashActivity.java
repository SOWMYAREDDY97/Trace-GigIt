package com.traceandgigit;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.parse.Parse;
import com.traceandgigit.model.DeviceRegData;
import com.traceandgigit.requests.DeviceRegistration;
import com.traceandgigit.requests.SharedUtils;
import com.traceandgigit.retrofit.APICallback;
import com.traceandgigit.retrofit.APIResponses;
import com.traceandgigit.retrofit.APIService;
import com.traceandgigit.retrofit.RetrofitClientInstance;


/*
    Splash Screen, that is opened when the app launches.
    This is cause it is Declared as Launcher Activity in the Manifest
 */
public class SplashActivity extends Activity {


    public static final int PERMISSION_REQUEST_CODE = 201;
    private static final int REQUEST_PERMISSION_SETTING = 401;
    ImageView appLogo;
    private boolean shouldNavigateToMain = true;
    private EditText scheme, host;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkPermissions(this);
    }



    private void initUI(){
        appLogo = findViewById(R.id.appLogo);
        appLogo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                shouldNavigateToMain = false;
                showConfigUI();
                return true;
            }
        });
        startTimerToLaunchMain();
    }
    private void checkDeviceRegistration(){

        DeviceRegistration registration = new DeviceRegistration(this, new APICallback<DeviceRegData>() {
            @Override
            public void onResponse(APIResponses<DeviceRegData> response) {
                if(response != null && response.body() != null && response.body().clientKey != null){
                    SharedUtils.getInstance(SplashActivity.this).setString(AppConstants.CLIENT_KEY,response.body().clientKey);
                    launchSignInActivity();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorCode) {
                Log.d(this.getClass().getCanonicalName(),t.toString());
                Toast.makeText(SplashActivity.this,"Device Reg failed",Toast.LENGTH_LONG).show();
            }
        });
        APIService.getInstance().execute(registration);

    }

    //Method that shows Config UI if the Image in SplashScreen is Long Clicked
    private void showConfigUI(){

        LinearLayout dynamicHostLayout = findViewById(R.id.dynamicHostLayout);
        dynamicHostLayout.setVisibility(View.VISIBLE);

        scheme = findViewById(R.id.scheme);
        host = findViewById(R.id.host);

        Button continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!scheme.getText().toString().isEmpty() && !host.getText().toString().isEmpty()){
                    RetrofitClientInstance.BASE_SCHEME = scheme.getText().toString();
                    RetrofitClientInstance.BASE_HOST = host.getText().toString();
                    AppConstants.DID_API_CHANGED = true;
                    //checkDeviceRegistration();

                }else{
                    Toast.makeText(SplashActivity.this,"Please enter Scheme and Host",Toast.LENGTH_LONG).show();
                }
            }
        });


    }
    private void startTimerToLaunchMain() {
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if(shouldNavigateToMain){
                    //checkDeviceRegistration();
                    launchSignInActivity();

                }
            }
        };
        handler.postDelayed(r, 3000);
    }

    private void launchSignInActivity(){
        Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (!ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showDialog(true);
                }else
                    showDialog(false);
            }
        }else{
            initUI();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showDialog(final boolean shouldShowRationale) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppCompatAlertDialogStyle);
        if (!shouldShowRationale) {
            alertDialogBuilder.setMessage(getString(R.string.permission_warning_text));
            alertDialogBuilder.setTitle(getString(R.string.permission_text));
        } else {
            alertDialogBuilder.setMessage(getString(R.string.permission_warning_settings));
            alertDialogBuilder.setTitle(getString(R.string.permission_text));
        }
        alertDialogBuilder.setPositiveButton(shouldShowRationale ? getString(R.string.accept_from_settings) : getString(R.string.accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (shouldShowRationale) {
                    //TODO: Redirect to settings
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                } else
                    checkPermissions(SplashActivity.this);

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void checkPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    String[] permissions;
                    permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
                    requestPermissions(permissions, PERMISSION_REQUEST_CODE);
                } else {
                    initUI();
                }
            }else{
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    String[] permissions;
                    permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
                    requestPermissions(permissions, PERMISSION_REQUEST_CODE);
                } else {
                    initUI();
                }
            }
        } else
            initUI();
    }

}
